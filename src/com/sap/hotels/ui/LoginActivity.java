package com.sap.hotels.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;

/**
 * Login view allows a user with an account to log in on a new installation
 * 
 * @author I838546
 * 
 */
public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";

	private final class ViewHolder {
		Button login;
		EditText email;
		EditText pwd;
	}

	private ViewHolder m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Get Views
		m = new ViewHolder();
		m.login = (Button) findViewById(R.id.button_login);
		m.email = (EditText) findViewById(R.id.email_field);
		m.pwd = (EditText) findViewById(R.id.pwd_field);

		// Set button listener
		m.login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = m.email.getText().toString();
				String pwd = m.pwd.getText().toString();

				// Check for valid credentials this encompasses lexical checks
				// and duplicate account checks
				if (credentialCheck(email, pwd)) {
					Log.d(TAG, "Got credentials " + email + ":" + pwd);
					// Save the new account information
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("pref_user_name", email);
					editor.putString("pref_password", pwd);
					editor.commit();

					// Launch the hotel list activity
					Intent i = new Intent(LoginActivity.this, HotelListActivity.class);
					LoginActivity.this.startActivity(i);
					// Otherwise the user may not pass
				} else {
					Toast.makeText(LoginActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * Lexical check and user exists check with MobiLink TODO: Account
	 * validation with MobiLink
	 * 
	 * @param email
	 *            Email address for account name
	 * @param pwd
	 *            Password
	 * @return True if valid false otherwise
	 */
	private boolean credentialCheck(String email, String pwd) {
		DBAccessor dba = DBAccessor.getInstance();
		try {
			dba.open(this);
			if (!email.equals("") && !pwd.equals("")) {
				if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
					if (dba.userExists(email))
						return true;
				}
			}
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(this, "Failed to lookup user", Toast.LENGTH_LONG).show();
		}

		return false;
	}
}
