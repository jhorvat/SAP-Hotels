package com.sap.hotels.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.sap.hotels.R;

/**
 * Settings class
 * 
 * @author I838546
 * 
 */
public class SettingsActivity extends Activity {

	/**
	 * Google told me to use fragments so I did
	 * 
	 * @author I838546
	 * 
	 */
	private static class SettingsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			// Get the about 'preference'
			Preference about = findPreference("pref_about");
			// Set a new listener for it
			about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {
					// Create a new alert dialog with credits in it
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(
							"SAP Hotel Co-op Project\nSummer 2013\n\nAndroid App: Julian Horvat\nWeb/Delphi App: Shawn Aitken\nDBA: Mikel Rychliski\nAfari: Ama Al-Abassi")
							.setTitle("About")
							.setPositiveButton("Close", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();

					return true;
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
