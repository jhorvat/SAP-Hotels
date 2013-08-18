package com.sap.hotels.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;

/**
 * Initial startup activity allows existing users to login and new ones to
 * create an account shouldn't see this much
 * 
 * @author I838546
 * 
 */
public class StartupActivity extends Activity {

	private class SyncDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the builder
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

			// Get the custom dialog layout
			LayoutInflater inflater = getActivity().getLayoutInflater();
			inflater.inflate(R.layout.sync_dialog, null);

			Log.d(TAG, "Inflating dialog box");

			return builder.create();
		}
	}

	private class SyncTask extends AsyncTask<Void, ULjException, Void> {

		private static final String TAG = "SyncTask";
		private DialogFragment d;

		/**
		 * Setup everything
		 */
		protected void onPreExecute() {
			Log.d(TAG, "Syncing...");
			d = new SyncDialog();
			d.show(StartupActivity.this.getFragmentManager(), "Initial Sync");
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Log.d(TAG, "Syncing db in background");

				DBAccessor dba = DBAccessor.getInstance();
				dba.open(StartupActivity.this);

				dba.sync("test_ml_user", "sql");
			} catch (ULjException e) {
				publishProgress(e);
			}
			return null;
		}

		/**
		 * Display exception toast on the UI thread
		 */
		protected void onProgressUpdate(ULjException... values) {
			Log.e(TAG, values[0].getMessage());
			Toast.makeText(StartupActivity.this, "MobiLink sync failed", Toast.LENGTH_LONG).show();
			m.createNewButton.setEnabled(false);
			m.loginButton.setEnabled(false);
			d.dismiss();
		}

		/**
		 * Finish up
		 */
		protected void onPostExecute(Void result) {
			Log.d(TAG, "ASyncTask completed, cleaning up and posting data");
			d.dismiss();
		}
	}

	private static final String TAG = "StartupActivity";

	private final class ViewHolder {
		Button loginButton, createNewButton;
	}

	protected ViewHolder m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);

		// Set defaults on first launch
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// Get views
		m = new ViewHolder();

		m.loginButton = (Button) findViewById(R.id.button_login);
		// Start either the login activity or new user activity
		m.loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Login button pushed");
				Intent i = new Intent(StartupActivity.this, LoginActivity.class);
				StartupActivity.this.startActivity(i);
			}
		});

		m.createNewButton = (Button) findViewById(R.id.button_new_account);
		// Start either the login activity or new user activity
		m.createNewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Create new button pushed");
				Intent i = new Intent(StartupActivity.this, CreateNewUserActivity.class);
				StartupActivity.this.startActivity(i);
			}
		});
	}

	protected void onStart() {
		super.onStart();
		// If the we have non-default credentials let the user through to the
		// hotel list
		if (haveCredentials()) {
			Log.d(TAG, "Have credentials, skipping to list");
			Intent i = new Intent(StartupActivity.this, HotelListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			StartupActivity.this.startActivity(i);
			finish();
		} else {
			new SyncTask().execute();
		}
	}

	/**
	 * Checks for non-default credentials
	 * 
	 * @return True if non-default false otherwise
	 */
	private boolean haveCredentials() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		// Love one liners
		return (!sp.getString("pref_user_name", "username").equals("username") && !sp.getString(
				"pref_password", "password").equals("password")) ? true : false;
	}
}
