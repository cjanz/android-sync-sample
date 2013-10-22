package de.bit.android.syncsample.rest;

import android.os.Handler;

public class TodoRestClient {

	public static Thread attemptAuth(final RestClientCredentials account,
			final Handler handler, final AuthenticationResultHandler context) {

		final Runnable runnable = new Runnable() {
			public void run() {
				authenticate(account, handler, context);
			}
		};

		return performOnBackgroundThread(runnable);
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
