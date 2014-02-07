package de.bit.android.syncsample.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import de.bit.android.syncsample.content.TodoEntity;

public class TodoRestClient {

	private static final String BACKEND_HOST = "10.0.2.2";
	public static final int TIMEOUT = 6 * 1000; // ms

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

		HttpURLConnection urlConnection = openConnection(getBackendUrl(), false);
		try {
			String json = readStringFromStreamString(urlConnection
					.getInputStream());

			final JSONArray todoArray = new JSONArray(json);
			for (int i = 0; i < todoArray.length(); i++) {
				todos.add(TodoEntity.fromJSON(todoArray.getJSONObject(i)));
			}
		} finally {
			urlConnection.disconnect();
		}

		return todos;
	}

	public static TodoEntity saveTodo(TodoEntity todoEntity)
			throws IOException, JSONException {

		HttpURLConnection urlConnection = openConnection(
				getBackendUrl(todoEntity.getServerId()), true);
		if (todoEntity.getServerId() == null) {
			urlConnection.setRequestMethod(HttpPost.METHOD_NAME);
		} else {
			urlConnection.setRequestMethod(HttpPut.METHOD_NAME);
		}

		try {
			String output = todoEntity.toJSON();
			OutputStream out = urlConnection.getOutputStream();
			writeStringToStream(output, out);

			String json = readStringFromStreamString(urlConnection
					.getInputStream());
			return TodoEntity.fromJSON(new JSONObject(json));
		} finally {
			urlConnection.disconnect();
		}
	}

	public static void deleteTodo(TodoEntity todoEntity) throws IOException {

		HttpURLConnection urlConnection = openConnection(
				getBackendUrl(todoEntity.getServerId()), false);
		urlConnection.setRequestMethod(HttpDelete.METHOD_NAME);

		try {
			readStringFromStreamString(urlConnection
					.getInputStream());
		} finally {
			urlConnection.disconnect();
		}
	}

	private static HttpURLConnection openConnection(URL url, boolean writable)
			throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setReadTimeout(TIMEOUT);
		urlConnection.setConnectTimeout(TIMEOUT);

		if (writable) {
			urlConnection.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			urlConnection.setDoOutput(true);
			urlConnection.setChunkedStreamingMode(0);
		}

		return urlConnection;
	}

	private static URL getBackendUrl(Long serverId)
			throws MalformedURLException {
		URL backendUrl = getBackendUrl();
		if (serverId != null) {
			backendUrl = new URL(backendUrl, String.valueOf(serverId));
		}
		return backendUrl;
	}

	private static URL getBackendUrl() throws MalformedURLException {
		URL url = new URL("http://" + BACKEND_HOST
				+ ":8080/syncsample-backend/rest/todo/");
		return url;
	}

	private static String readStringFromStreamString(InputStream is)
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

	private static void writeStringToStream(String output, OutputStream out)
			throws IOException {

		out.write(output.getBytes("UTF-8"));
		out.flush();
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
