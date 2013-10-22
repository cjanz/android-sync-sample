package de.bit.android.syncsample.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Handler;
import de.bit.android.syncsample.content.TodoEntity;

public class TodoRestClient {

	private static final String BACKEND_HOST = "192.168.1.23";
	public static final int TIMEOUT = 3 * 1000; // ms

	public static Thread attemptAuth(final RestClientCredentials account,
			final Handler handler, final AuthenticationResultHandler context) {

		final Runnable runnable = new Runnable() {
			public void run() {
				authenticate(account, handler, context);
			}
		};

		return performOnBackgroundThread(runnable);
	}

	public static List<TodoEntity> loadAllTodos() throws IOException,
			JSONException {

		List<TodoEntity> todos = new ArrayList<TodoEntity>();

		URL url = new URL("http://" + BACKEND_HOST
				+ ":8080/syncsample-backend/rest/todo");
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		try {
			String json = convertStreamToString(urlConnection.getInputStream());

			final JSONArray todoArray = new JSONArray(json);
			for (int i = 0; i < todoArray.length(); i++) {
				todos.add(TodoEntity.fromJSON(todoArray.getJSONObject(i)));
			}
		} finally {
			urlConnection.disconnect();
		}

		return todos;
	}

	private static String convertStreamToString(InputStream is)
			throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		is.close();
		return sb.toString();
	}

	private static boolean authenticate(RestClientCredentials account,
			Handler handler, final AuthenticationResultHandler context) {

		boolean result = false;
		try {
			result = postCredentials(account);
		} catch (Exception e) {
			result = false;
		}

		sendResult(result, handler, context);
		return result;
	}

	private static boolean postCredentials(RestClientCredentials account)
			throws InterruptedException {

		Thread.sleep(3000);
		return "test".equals(account.getPassword());
	}

	private static void sendResult(final Boolean result, final Handler handler,
			final AuthenticationResultHandler context) {

		if (handler == null || context == null) {
			return;
		}

		handler.post(new Runnable() {
			public void run() {
				context.onAuthenticationResult(result);
			}
		});
	}

	private static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}

}
