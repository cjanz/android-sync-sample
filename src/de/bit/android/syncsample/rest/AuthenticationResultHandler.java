package de.bit.android.syncsample.rest;

/**
 * Callback interface for authentication requests.
 */
public interface AuthenticationResultHandler {

	/**
	 * Callback that is executed when the authentication has finished.
	 * 
	 * @param result
	 *            true if the authentication has been successful, false
	 *            otherwise
	 */
	void onAuthenticationResult(boolean result);

}
