package de.bit.android.syncsample.content;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

public class TodoEntity {

	public enum SyncState {
		NOOP, CREATE, UPDATE, REMOVE, CONFLICTED
	}

	static final String TABLE_NAME = "TODO";

	public static final String SERVER_VERSION = "SERVER_VERSION";

	public static final String CONFLICT_SERVER_VERSION = "CONFLICT_SERVER_VERSION";

	public static final String SERVER_ID = "SERVER_ID";

	public static final String TITLE = "TITLE";

	public static final String TEXT = "TODO_TEXT";

	public static final String ID = "_id";

	public static final String SYNC_STATE = "SYNC_STATE";

	private Long id;

	private Long serverId;

	private Long serverVersion;

	private Long conflictedServerVersion;

	private String title;

	private String text;

	private SyncState syncState = SyncState.NOOP;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public Long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}

	public Long getConflictedServerVersion() {
		return conflictedServerVersion;
	}

	public void setConflictedServerVersion(Long conflictedServerVersion) {
		this.conflictedServerVersion = conflictedServerVersion;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public SyncState getSyncState() {
		return syncState;
	}

	public void setSyncState(SyncState syncState) {
		this.syncState = syncState;
	}

	public static TodoEntity fromJSON(JSONObject jsonObject)
			throws JSONException {
		if (jsonObject == null) {
			return null;
		}

		TodoEntity entity = new TodoEntity();

		entity.setServerId(jsonObject.getLong("id"));
		entity.setServerVersion(jsonObject.getLong("version"));
		entity.setTitle(jsonObject.getString("title"));
		entity.setText(jsonObject.getString("text"));
		entity.setSyncState(SyncState.NOOP);

		return entity;
	}

	public String toJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", serverId);
		jsonObject.put("version", serverVersion);
		jsonObject.put("title", title);
		jsonObject.put("text", text);
		return jsonObject.toString();
	}

	public static TodoEntity fromCursor(Cursor cursor) {
		if (cursor == null) {
			return null;
		}

		TodoEntity entity = new TodoEntity();

		if (!cursor.isNull(cursor.getColumnIndexOrThrow(SERVER_ID))) {
			entity.setServerId(cursor.getLong(cursor
					.getColumnIndexOrThrow(SERVER_ID)));
		}
		if (!cursor.isNull(cursor.getColumnIndexOrThrow(SERVER_VERSION))) {
			entity.setServerVersion(cursor.getLong(cursor
					.getColumnIndexOrThrow(SERVER_VERSION)));
		}
		if (!cursor.isNull(cursor
				.getColumnIndexOrThrow(CONFLICT_SERVER_VERSION))) {
			entity.setConflictedServerVersion(cursor.getLong(cursor
					.getColumnIndexOrThrow(CONFLICT_SERVER_VERSION)));
		}

		entity.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
		entity.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
		entity.setText(cursor.getString(cursor.getColumnIndexOrThrow(TEXT)));
		entity.setSyncState(SyncState.valueOf(cursor.getString(cursor
				.getColumnIndexOrThrow(SYNC_STATE))));

		return entity;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SERVER_ID, serverId);
		values.put(SERVER_VERSION, serverVersion);
		values.put(CONFLICT_SERVER_VERSION, conflictedServerVersion);
		values.put(TITLE, title);
		values.put(TEXT, text);
		values.put(SYNC_STATE, syncState.name());
		return values;
	}

}
