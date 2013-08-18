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
 * Activity to create a new user account
 * @author I838546
 *
 */
public class CreateNewUserActivity extends Activity {

	private static final String TAG = "CreateNewUserActivity";

	private final class ViewHolder {
		Button create;
		EditText name;
		EditText email;
		EditText pwd;
	}

	private ViewHolder m;
	private DBAccessor dba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_user);

		dba = DBAccessor.getInstance();
		try {
			dba.open(this);
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m = new ViewHolder();
		m.create = (Button) findViewById(R.id.button_submit);
		m.name = (EditText) findViewById(R.id.field_name);
		m.email = (EditText) findViewById(R.id.field_email);
		m.pwd = (EditText) findViewById(R.id.field_pwd);

		m.create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String name = m.name.getText().toString();
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

					Intent i = new Intent(CreateNewUserActivity.this, HotelListActivity.class);
					CreateNewUserActivity.this.startActivity(i);
					// Otherwise the user may not pass
				} else {
					Toast.makeText(CreateNewUserActivity.this, "Failed to register new user!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Lexical and duplicate account checks with MobiLink TODO: Account
	 * validation with MobiLink
	 * 
	 * @param email
	 *            Email address for account name
	 * @param pwd
	 *            Password
	 * @return True if valid false otherwise
	 */
	private boolean credentialCheck(String email, String pwd) {
		if (!email.equals("") && !pwd.equals("")) {
			if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
				try {
					if (!dba.userExists(email))
						return true;
				} catch (ULjException e) {
					Log.e(TAG, e.getMessage());
					Toast.makeText(this, "Failed to lookup user", Toast.LENGTH_LONG).show();
				}
			}
		}
		return false;
	}
}
