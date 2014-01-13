/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.bit.android.syncsample.authenticator;

import static android.util.Log.*;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle Account authentication.
 * 
 * It instantiates the {@link Authenticator} and returns its {@link IBinder}.
 */
public class AuthenticatorService extends Service {

	private static final String TAG = "AuthenticatorService";

	private Authenticator authenticator;

	@Override
	public void onCreate() {
		if (isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "AuthenticatorService started.");
		}

		authenticator = new Authenticator(this);
	}

	@Override
	public void onDestroy() {
		if (isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "AuthenticatorService stopped.");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "Returning the Authenticator binder for intent "
					+ intent);
		}

		return authenticator.getIBinder();
	}
}
