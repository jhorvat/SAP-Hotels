package com.sap.hotels.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;

/**
 * Subclass of detailed room view this one lets you reserve a room
 * 
 * @author I838546
 * 
 */
public class ReserveRoomActivity extends RoomInfoBaseActivity {

	/**
	 * View holder pattern
	 * 
	 * @author I838546
	 * 
	 */
	private class ViewHolder extends RoomInfoBaseActivity.VH {
		Button reserve;
	}

	private static final String TAG = "RoomViewActivity";

	private ViewHolder m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m = new ViewHolder();
		m.reserve = (Button) findViewById(R.id.reserveButton);
		m.reserve.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					// TODO: Add a customer ID lookup here for now the test_user
					// is hardcoded
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					dba.addReservation(dba.getCustId(sp.getString("pref_user_name", "")), roomId,
							startT, endT);
					m.reserve.setText("Reserved");
					m.reserve.setEnabled(false);
				} catch (ULjException e) {
					Log.e(TAG, e.getMessage());
					Toast.makeText(arg0.getContext(), "Failed to add reservation",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent i = new Intent(this, HotelViewActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_reserve_room;
	}

}
