package de.bit.android.syncsample.content;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class TodoEntity {

	public enum SyncState {
        NOOP, CREATE, UPDATE, REMOVE
    }
	
	static final String TABLE_NAME = "TODO";

	public static final String SERVER_VERSION = "SERVER_VERSION";

	public static final String SERVER_ID = "SERVER_ID";

	public static final String TITLE = "TITLE";
	
	public static final String TEXT = "TODO_TEXT";

	public static final String ID = "_id";
	
	public static final String SYNC_STATE = "SYNC_STATE";

	private Long id;

	private Long serverId;

	private Long serverVersion;

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

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SERVER_ID, serverId);
		values.put(SERVER_VERSION, serverVersion);
		values.put(TITLE, title);
		values.put(TEXT, text);
		values.put(SYNC_STATE, syncState.name());
		return values;
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

}
