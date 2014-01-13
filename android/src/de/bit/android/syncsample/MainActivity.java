package de.bit.android.syncsample;

import static de.bit.android.syncsample.authenticator.LoginActivity.*;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;

/**
 * {@link ListActivity} that display the Todos from the local ContentProvider.
 * 
 * @see {@link TodoContentProvider}
 */
public class MainActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;
	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Account[] accounts = AccountManager.get(this).getAccountsByType(
				ACCOUNT_TYPE);
		if (accounts.length > 0) {
			account = accounts[0];
		}

		fillData();
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
		}

		return super.onOptionsItemSelected(item);
	}

	private void fillData() {

		// Bind column TITLE to field "label"
		String[] from = new String[] { TodoEntity.TITLE };
		int[] to = new int[] { R.id.label };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,
				to, 0);

		setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] projection = { TodoEntity.ID, TodoEntity.TITLE };

		CursorLoader cursorLoader = new CursorLoader(this,
				TodoContentProvider.CONTENT_URI, projection, null, null,
				TodoEntity.TITLE + " COLLATE NOCASE ASC");
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

}
