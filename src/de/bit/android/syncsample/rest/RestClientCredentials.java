package de.bit.android.syncsample.rest;

/**
 * The Credentials for the REST calls.
 */
public class RestClientCredentials {

	private String username;

	private String password;

	public RestClientCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
