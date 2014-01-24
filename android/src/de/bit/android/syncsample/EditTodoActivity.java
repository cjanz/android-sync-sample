package de.bit.android.syncsample;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.content.TodoEntity;

public class EditTodoActivity extends Activity {

	protected static final String EXTRA_ID = "EDIT_TODO_ID";

	private EditText titleEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_todo);

		this.titleEdit = (EditText) findViewById(R.id.titleEdit);

		bindModelToView();
	}

	private void bindModelToView() {
		long id = getIntent().getExtras().getLong(EXTRA_ID, -1);
		if (id == -1) {
			return;
		}
		
		Cursor cursor = getContentResolver().query(
				TodoContentProvider.getUri(id), null, null, null, null);
		
		if (cursor.moveToFirst()) {
			this.titleEdit.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TodoEntity.TITLE)));
		}
		cursor.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_todo, menu);
		return true;
	}

	public void cancel(View view) {
		finish();
	}

}
