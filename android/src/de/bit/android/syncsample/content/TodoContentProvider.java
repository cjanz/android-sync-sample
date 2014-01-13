package de.bit.android.syncsample.content;

import static android.content.ContentResolver.*;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Implementation of the {@link ContentProvider} for the authority
 * {@value #AUTHORITY}.
 */
public class TodoContentProvider extends ContentProvider {

	private static final int TODOS = 10;
	private static final int TODO_ID = 20;

	private static final String BASE_PATH = "todos";

	public static final String AUTHORITY = "de.bit.android.syncsample.content";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	private static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/todos";
	private static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE
			+ "/todo";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
	}

	private DatabaseHelper databaseHelper;

	public static Uri getUri(Long id) {
		return Uri.parse(CONTENT_URI + "/" + id);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());

		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);

		int rowsDeleted = 0;

		switch (uriType) {
		case TODOS:
			rowsDeleted = sqlDB.delete(TodoEntity.TABLE_NAME, selection,
					selectionArgs);
			break;
		case TODO_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(TodoEntity.TABLE_NAME, TodoEntity.ID
						+ "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(TodoEntity.TABLE_NAME, TodoEntity.ID
						+ "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);

		switch (uriType) {
		case TODOS:
			return CONTENT_TYPE;
		case TODO_ID:
			return CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Set the table
		queryBuilder.setTables(TodoEntity.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case TODOS:
			break;
		case TODO_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(TodoEntity.ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);

		long id = 0;
		switch (uriType) {
		case TODOS:
			id = sqlDB.insert(TodoEntity.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);

		int rowsUpdated = 0;

		switch (uriType) {
		case TODOS:
			rowsUpdated = sqlDB.update(TodoEntity.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case TODO_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(TodoEntity.TABLE_NAME, values,
						TodoEntity.ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(TodoEntity.TABLE_NAME, values,
						TodoEntity.ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

}
