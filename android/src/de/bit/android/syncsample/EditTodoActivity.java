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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;
import de.bit.android.syncsample.content.TodoEntity.SyncState;

public class EditTodoActivity extends Activity {

	public static final String EXTRA_ID = "EDIT_TODO_ID";

	private EditText titleEdit;
	private EditText textEdit;
	private ViewGroup conflictPanel;
	private Button saveButton;

	private TodoEntity todoEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_todo);

		this.titleEdit = (EditText) findViewById(R.id.titleEdit);
		this.textEdit = (EditText) findViewById(R.id.textEdit);
		this.conflictPanel = (ViewGroup) findViewById(R.id.conflictPanel);
		this.saveButton = (Button) findViewById(R.id.saveButton);

		bindModelToView();
	}

	private void bindModelToView() {
		Long id = getRecordId();
		if (id == null) {
			this.conflictPanel.setVisibility(View.GONE);
			return;
		}

		Cursor cursor = getContentResolver().query(
				TodoContentProvider.getUri(id), null, null, null, null);

		if (cursor.moveToFirst()) {
			this.todoEntity = TodoEntity.fromCursor(cursor);

			if (SyncState.CONFLICTED.equals(todoEntity.getSyncState())) {
				this.conflictPanel.setVisibility(View.VISIBLE);
				this.saveButton.setEnabled(false);
			} else {
				this.conflictPanel.setVisibility(View.GONE);
				this.saveButton.setEnabled(true);
			}

			this.titleEdit.setText(todoEntity.getTitle());
			this.textEdit.setText(todoEntity.getText());
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

	public void overwriteLocal(View view) {
		Long recordId = getRecordId();

		if (recordId != null) {
			ContentValues values = new ContentValues();
			values.put(TodoEntity.SYNC_STATE, SyncState.NOOP.name());
			getContentResolver().update(TodoContentProvider.getUri(recordId),
					values, null, null);

			requestDownload();
			finish();
		}
	}

	public void overwriteServer(View view) {
		Long recordId = getRecordId();

		if (recordId != null) {
			ContentValues values = new ContentValues();
			values.put(TodoEntity.SERVER_VERSION,
					todoEntity.getConflictedServerVersion());
			values.putNull(TodoEntity.CONFLICT_SERVER_VERSION);
			getContentResolver().update(TodoContentProvider.getUri(recordId),
					values, null, null);

			save(view);
		}
	}

	private void requestDownload() {
		requestSync(false);
	}

	private void requestUpload() {
		requestSync(true);
	}

	private void requestSync(boolean upload) {
		Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, upload);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

		ContentResolver.requestSync(getDefaultAccount(this),
				TodoContentProvider.AUTHORITY, extras);
	}

}
