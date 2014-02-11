package de.bit.android.syncsample;

import static android.content.ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
import static android.content.ContentResolver.SYNC_OBSERVER_TYPE_PENDING;
import static android.content.ContentResolver.isSyncActive;
import static android.view.Window.FEATURE_INDETERMINATE_PROGRESS;
import android.accounts.Account;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.bit.android.syncsample.authenticator.Authenticator;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;
import de.bit.android.syncsample.content.TodoEntity.SyncState;

/**
 * {@link ListActivity} that display the Todos from the local ContentProvider.
 * 
 * @see {@link TodoContentProvider}
 */
public class MainActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, SyncStatusObserver {

	private final class TodoRowViewBinder implements
			SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (columnIndex == cursor.getColumnIndex(TodoEntity.SYNC_STATE)) {
				SyncState syncState = SyncState.valueOf(cursor
						.getString(columnIndex));
				((TextView) view).setCompoundDrawablesWithIntrinsicBounds(
						syncState.getIconId(), 0, 0, 0);
				return true;
			} else if (columnIndex == cursor.getColumnIndex(TodoEntity.TITLE)) {
				((TextView) view).setText(cursor.getString(columnIndex));
				return true;
			} else {
				return false;
			}
		}
	}

	private SimpleCursorAdapter adapter;
	private Account account;

	private Object syncObserverHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		account = Authenticator.getDefaultAccount(this);

		fillData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateProgressBarVisibility();
		syncObserverHandle = ContentResolver.addStatusChangeListener(
				SYNC_OBSERVER_TYPE_ACTIVE | SYNC_OBSERVER_TYPE_PENDING, this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (syncObserverHandle != null) {
			ContentResolver.removeStatusChangeListener(syncObserverHandle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_sync && account != null) {

			Bundle params = new Bundle();
			params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

			ContentResolver.requestSync(account, TodoContentProvider.AUTHORITY,
					params);
			return true;
		} else if (item.getItemId() == R.id.action_create) {

			openEditActivity(null);
		}

		return super.onOptionsItemSelected(item);
	}

	private void fillData() {

		// Bind column TITLE to field "label"
		String[] from = new String[] { TodoEntity.SYNC_STATE, TodoEntity.TITLE };
		int[] to = new int[] { R.id.label, R.id.label };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,
				to, 0);
		adapter.setViewBinder(new TodoRowViewBinder());

		setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] projection = { TodoEntity.ID, TodoEntity.SYNC_STATE,
				TodoEntity.TITLE };

		CursorLoader cursorLoader = new CursorLoader(this,
				TodoContentProvider.CONTENT_URI, projection,
				TodoEntity.SYNC_STATE + " != ?",
				new String[] { SyncState.REMOVE.name() }, TodoEntity.TITLE
						+ " COLLATE NOCASE ASC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		openEditActivity(id);
	}

	private void openEditActivity(Long id) {
		Intent intent = new Intent(this, EditTodoActivity.class);
		intent.putExtra(EditTodoActivity.EXTRA_ID, id);
		startActivity(intent);
	}

	@Override
	public void onStatusChanged(int which) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateProgressBarVisibility();
			}
		});
	}

	private void updateProgressBarVisibility() {
		if (account == null) {
			setProgressBarIndeterminateVisibility(false);
			return;
		}

		boolean isSyncActive = isSyncActive(account,
				TodoContentProvider.AUTHORITY);
		setProgressBarIndeterminateVisibility(isSyncActive);

	}

}
