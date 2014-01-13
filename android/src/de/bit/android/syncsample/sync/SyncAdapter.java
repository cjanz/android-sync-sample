package de.bit.android.syncsample.sync;

import static de.bit.android.syncsample.content.TodoContentProvider.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;
import de.bit.android.syncsample.rest.TodoRestClient;

/**
 * Implementation of {@link AbstractThreadedSyncAdapter} that contains the logic
 * to sync data from the REST backend to the local database.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG = "SyncAdapter";

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		Log.d(TAG, "Sync started");

		try {
			// get Todos from Backend
			List<TodoEntity> allTodos = TodoRestClient.loadAllTodos();
			Map<Long, TodoEntity> serverIdIndex = getIdIndex(allTodos);

			// get Todos from local DB
			Cursor cursor = provider.query(CONTENT_URI, new String[] {
					TodoEntity.ID, TodoEntity.SERVER_ID }, null, null,
					TodoEntity.SERVER_ID);

			for (boolean more = cursor.moveToFirst(); more; more = cursor
					.moveToNext()) {

				long serverId = cursor.getLong(cursor
						.getColumnIndexOrThrow(TodoEntity.SERVER_ID));
				long internalId = cursor.getLong(cursor
						.getColumnIndexOrThrow(TodoEntity.ID));
				Uri uri = TodoContentProvider.getUri(internalId);

				if (serverIdIndex.containsKey(serverId)) {
					TodoEntity todo = serverIdIndex.remove(serverId);

					// Backend + Local DB --> Update
					provider.update(uri, todo.toContentValues(), null, null);
				} else {
					// Only local DB --> Delete
					provider.delete(uri, null, null);
				}
			}

			// Insert new Todos from Backend
			for (TodoEntity todo : serverIdIndex.values()) {
				provider.insert(CONTENT_URI, todo.toContentValues());
			}

		} catch (Exception e) {
			Log.w(TAG, "Error synchronizing todos", e);
			syncResult.stats.numIoExceptions++;
		}

		Log.d(TAG, "Sync finished");
	}

	private static Map<Long, TodoEntity> getIdIndex(List<TodoEntity> items) {
		Map<Long, TodoEntity> map = new HashMap<Long, TodoEntity>();
		for (TodoEntity todo : items) {
			map.put(todo.getServerId(), todo);
		}
		return map;
	}

}
