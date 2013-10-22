package de.bit.android.syncsample.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Implementation of {@link AbstractThreadedSyncAdapter} that contains the logic
 * to sync data from the REST backend to the local database.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

	}

}
