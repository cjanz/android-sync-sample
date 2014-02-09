package de.bit.android.syncsample.sync;

import static android.text.TextUtils.join;
import static de.bit.android.syncsample.content.TodoContentProvider.CONTENT_URI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import de.bit.android.syncsample.EditTodoActivity;
import de.bit.android.syncsample.R;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;
import de.bit.android.syncsample.content.TodoEntity.SyncState;
import de.bit.android.syncsample.rest.TodoRestClient;
import de.bit.android.syncsample.rest.UpdateConflictException;

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

		syncChangedRecordsToBackend(provider, syncResult);
		syncDeletedRecordsToBackend(provider, syncResult);

		if (!extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD)) {
			syncBackendToLocalDB(provider, syncResult);
		}

		Log.d(TAG, "Sync finished");
	}

	private void syncChangedRecordsToBackend(ContentProviderClient provider,
			SyncResult syncResult) {
		try {
			Cursor changedRecordsCursor = provider.query(
					CONTENT_URI,
					null,
					TodoEntity.SYNC_STATE
							+ " IN ( '"
							+ join("', '", new SyncState[] { SyncState.CREATE,
									SyncState.UPDATE }) + "' )", null,
					TodoEntity.SERVER_ID);

			for (boolean more = changedRecordsCursor.moveToFirst(); more; more = changedRecordsCursor
					.moveToNext()) {
				TodoEntity todoEntity = TodoEntity
						.fromCursor(changedRecordsCursor);
				Long id = todoEntity.getId();
				Uri localrecordUri = TodoContentProvider.getUri(id);

				try {
					todoEntity = TodoRestClient.saveTodo(todoEntity);
					provider.update(localrecordUri,
							todoEntity.toContentValues(), null, null);
				} catch (FileNotFoundException e) {
					Log.w(TAG, "Updated record was deleted on backend: "
							+ todoEntity.getServerId());
					provider.delete(localrecordUri, null, null);
				} catch (UpdateConflictException e) {
					handleUpdateConflict(provider, todoEntity, localrecordUri,
							e);
				}
			}
		} catch (Exception e) {
			Log.w(TAG, "Error writing changed records to backend", e);
			syncResult.stats.numIoExceptions++;
		}
	}

	private void handleUpdateConflict(ContentProviderClient provider,
			TodoEntity todoEntity, Uri localTodoUri,
			UpdateConflictException exception) throws RemoteException {

		Log.w(TAG,
				"Update conflict detected for ID: " + todoEntity.getServerId());

		ContentValues values = new ContentValues();
		values.put(TodoEntity.SYNC_STATE, SyncState.CONFLICTED.name());
		values.put(TodoEntity.CONFLICT_SERVER_VERSION, exception
				.getConflictedEntity().getServerVersion());
		provider.update(localTodoUri, values, null, null);

		Intent intent = new Intent(getContext(), EditTodoActivity.class);
		intent.putExtra(EditTodoActivity.EXTRA_ID,
				Long.valueOf(todoEntity.getId().longValue()));
		@SuppressWarnings("deprecation")
		Notification notification = new Notification.Builder(getContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(
						PendingIntent.getActivity(getContext(), todoEntity
								.getId().hashCode(), intent,
								PendingIntent.FLAG_UPDATE_CURRENT))
				.setContentTitle("Update conflict for Todo")
				.setContentText(todoEntity.getTitle()).getNotification();

		NotificationManager notificationManager = (NotificationManager) getContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(todoEntity.getId().hashCode(), notification);
	}

	private void syncDeletedRecordsToBackend(ContentProviderClient provider,
			SyncResult syncResult) {
		try {
			Cursor deletedRecordsCursor = provider.query(CONTENT_URI, null,
					TodoEntity.SYNC_STATE + " = ?",
					new String[] { SyncState.REMOVE.name() },
					TodoEntity.SERVER_ID);

			for (boolean more = deletedRecordsCursor.moveToFirst(); more; more = deletedRecordsCursor
					.moveToNext()) {
				TodoEntity todoEntity = TodoEntity
						.fromCursor(deletedRecordsCursor);
				Long id = todoEntity.getId();
				Uri localrecordUri = TodoContentProvider.getUri(id);

				TodoRestClient.deleteTodo(todoEntity);
				provider.delete(localrecordUri, null, null);
			}
		} catch (Exception e) {
			Log.w(TAG, "Error writing deleted records to backend", e);
			syncResult.stats.numIoExceptions++;
		}
	}

	private void syncBackendToLocalDB(ContentProviderClient provider,
			SyncResult syncResult) {
		try {
			Map<Long, TodoEntity> serverIdIndex = loadAllTodosFromBackend();
			Cursor cursor = loadAllTodosFromLocalDB(provider);

			for (boolean more = cursor.moveToFirst(); more; more = cursor
					.moveToNext()) {
				processExistingRecord(provider, serverIdIndex, cursor);
			}

			// Insert new Todos from Backend
			for (TodoEntity todo : serverIdIndex.values()) {
				provider.insert(CONTENT_URI, todo.toContentValues());
			}

		} catch (Exception e) {
			Log.w(TAG, "Error synchronizing todos from backend", e);
			syncResult.stats.numIoExceptions++;
		}
	}

	private Map<Long, TodoEntity> loadAllTodosFromBackend() throws IOException,
			JSONException {

		List<TodoEntity> allTodos = TodoRestClient.loadAllTodos();
		Map<Long, TodoEntity> serverIdIndex = getIdIndex(allTodos);
		return serverIdIndex;
	}

	private Cursor loadAllTodosFromLocalDB(ContentProviderClient provider)
			throws RemoteException {

		return provider.query(CONTENT_URI, new String[] { TodoEntity.ID,
				TodoEntity.SERVER_ID, TodoEntity.SYNC_STATE }, null, null,
				TodoEntity.SERVER_ID);
	}

	private void processExistingRecord(ContentProviderClient provider,
			Map<Long, TodoEntity> serverIdIndex, Cursor cursor)
			throws RemoteException {

		long serverId = cursor.getLong(cursor
				.getColumnIndexOrThrow(TodoEntity.SERVER_ID));
		long internalId = cursor.getLong(cursor
				.getColumnIndexOrThrow(TodoEntity.ID));
		String syncState = cursor.getString(cursor
				.getColumnIndexOrThrow(TodoEntity.SYNC_STATE));
		Uri uri = TodoContentProvider.getUri(internalId);

		if (serverIdIndex.containsKey(serverId)) {
			TodoEntity todo = serverIdIndex.remove(serverId);

			// Backend + Local DB w/o changes --> Update
			if (SyncState.NOOP.name().equals(syncState)) {
				provider.update(uri, todo.toContentValues(), null, null);
			}
		} else if (!SyncState.CREATE.name().equals(syncState)) {
			// Only local DB and not a new record --> Delete
			provider.delete(uri, null, null);
		}
	}

	private static Map<Long, TodoEntity> getIdIndex(List<TodoEntity> items) {
		Map<Long, TodoEntity> map = new HashMap<Long, TodoEntity>();
		for (TodoEntity todo : items) {
			map.put(todo.getServerId(), todo);
		}
		return map;
	}

}
