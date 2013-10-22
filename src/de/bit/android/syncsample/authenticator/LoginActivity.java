package de.bit.android.syncsample.authenticator;

import static android.R.drawable.*;
import static android.view.Window.*;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.bit.android.syncsample.R;
import de.bit.android.syncsample.content.TodoContentProvider;
import de.bit.android.syncsample.rest.AuthenticationResultHandler;
import de.bit.android.syncsample.rest.RestClientCredentials;
import de.bit.android.syncsample.rest.TodoRestClient;

/**
 * Implementation of {@link AccountAuthenticatorActivity} that allows the user
 * to create a new account.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements
		AuthenticationResultHandler {

	private static final String CONTENT_AUTHORITY = TodoContentProvider.AUTHORITY;
	private static final String ACCOUNT_TYPE = "de.bit.android.sample.account";
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

	private EditText usernameEdit;
	private EditText passwordEdit;
	private TextView message;

	private Thread authThread;
	private Handler handler;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(FEATURE_LEFT_ICON);
		getWindow().setFeatureDrawableResource(FEATURE_LEFT_ICON,
				ic_dialog_info);

		setContentView(R.layout.activity_login);

		handler = new Handler();
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		message = (TextView) findViewById(R.id.message);

	}

	/**
	 * Click handler for the login button.
	 * 
	 * @param view
	 */
	public void handleLogin(View view) {
		String userName = usernameEdit.getText().toString();
		String password = passwordEdit.getText().toString();

		if (TextUtils.isEmpty(userName)) {
			message.setText(getText(R.string.msg_empty_username));
		} else {
			showProgress();
			authThread = TodoRestClient.attemptAuth(new RestClientCredentials(
					userName, password), handler, this);
		}
	}

	/**
	 * Shows the progress UI for a lengthy operation.
	 */
	protected void showProgress() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this,
					getText(R.string.app_name),
					getText(R.string.msg_authenticating));

			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						public void onCancel(DialogInterface dialog) {
							if (authThread != null) {
								authThread.interrupt();
								finish();
							}
						}

					});
		}

		progressDialog.show();
	}

	/**
	 * Hides the progress UI for a lengthy operation.
	 */
	protected void hideProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onAuthenticationResult(boolean result) {
		hideProgress();

		if (result) {
			createAccount();
		} else {
			message.setText(getText(R.string.msg_login_failed));
		}

	}

	private void createAccount() {
		String userName = usernameEdit.getText().toString();
		String password = passwordEdit.getText().toString();

		Account newAccount = new Account(userName, ACCOUNT_TYPE);

		AccountManager accountManager = AccountManager.get(this);
		accountManager.addAccountExplicitly(newAccount, password, null);

		configureSync(newAccount);

		Intent i = new Intent();
		i.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
		i.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
		i.putExtra(AccountManager.KEY_AUTHTOKEN, password);
		i.putExtra(AccountManager.KEY_PASSWORD, password);

		this.setAccountAuthenticatorResult(i.getExtras());
		this.setResult(RESULT_OK, i);

		finish();
	}

	private void configureSync(Account account) {
		ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
		ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);

		Bundle params = new Bundle();
		params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
		params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
		params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
		ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, params, 60);

		ContentResolver.requestSync(account, CONTENT_AUTHORITY, params);
	}

}
