package de.bit.android.syncsample.content;

import android.content.ContentValues;

public class TodoEntity {

	static final String TABLE_NAME = "TODO";

	public static final String SERVER_VERSION = "SERVER_VERSION";

	public static final String SERVER_ID = "SERVER_ID";

	public static final String TITLE = "TITLE";

	public static final String ID = "_id";

	private Long id;

	private Long serverId;

	private Long serverVersion;

	private String title;

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

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(SERVER_ID, serverId);
		values.put(SERVER_VERSION, serverVersion);
		values.put(TITLE, title);
		return values;
	}

}
