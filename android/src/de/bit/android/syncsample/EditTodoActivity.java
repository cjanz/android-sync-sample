package de.bit.android.syncsample;

import static de.bit.android.syncsample.authenticator.Authenticator.getDefaultAccount;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;
import de.bit.android.syncsample.content.TodoEntity.SyncState;

public class EditTodoActivity extends Activity {

	protected static final String EXTRA_ID = "EDIT_TODO_ID";

	private EditText titleEdit;
	private EditText textEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_todo);

		this.titleEdit = (EditText) findViewById(R.id.titleEdit);
		this.textEdit = (EditText) findViewById(R.id.textEdit);

		bindModelToView();
	}

	private void bindModelToView() {
		Long id = getRecordId();
		if (id == null) {
			return;
		}

		Cursor cursor = getContentResolver().query(
				TodoContentProvider.getUri(id), null, null, null, null);

		if (cursor.moveToFirst()) {
			this.titleEdit.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TodoEntity.TITLE)));
			this.textEdit.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TodoEntity.TEXT)));
		}
		cursor.close();
	}

	private Long getRecordId() {
		long id = getIntent().getExtras().getLong(EXTRA_ID, -1);
		if (id == -1) {
			return null;
		}
		return id;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_todo, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.action_delete) {
			delete();
		}
		
		return true;
	}

	private void delete() {
		Long recordId = getRecordId();
		
		if (recordId != null) {
			ContentValues values = new ContentValues();
			values.put(TodoEntity.SYNC_STATE, SyncState.REMOVE.name());
			getContentResolver().update(TodoContentProvider.getUri(recordId),
					values, null, null);
			
			requestUpload();
			finish();
		}
	}

	public void cancel(View view) {
		finish();
	}

	public void save(View view) {
		ContentValues values = new ContentValues();
		values.put(TodoEntity.TITLE, titleEdit.getText().toString());
		values.put(TodoEntity.TEXT, textEdit.getText().toString());

		Long recordId = getRecordId();

		if (recordId != null) {
			values.put(TodoEntity.SYNC_STATE, SyncState.UPDATE.name());
			getContentResolver().update(TodoContentProvider.getUri(recordId),
					values, null, null);
		} else {
			values.put(TodoEntity.SYNC_STATE, SyncState.CREATE.name());
			getContentResolver()
					.insert(TodoContentProvider.CONTENT_URI, values);
		}

		requestUpload();
		finish();
	}

	private void requestUpload() {
		Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		
		ContentResolver.requestSync(getDefaultAccount(this),
				TodoContentProvider.AUTHORITY, extras);
	}

}
