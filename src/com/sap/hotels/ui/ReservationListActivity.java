package com.sap.hotels.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;
import com.sap.hotels.db.Reservation;
import com.sap.hotels.ui.adapters.ReservationListAdapter;

/**
 * Lists the user's future reservations
 * 
 * @author I838546
 * 
 */
public class ReservationListActivity extends ListActivity {

	private static final String TAG = "CustomerReservationsActivity";
	private DBAccessor dba;
	private ArrayList<Reservation> results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_reservations);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get singleton instance
		dba = DBAccessor.getInstance();

		try {
			dba.open(this);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(this, "Failed to open db", Toast.LENGTH_LONG).show();
		}

		search();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent i = new Intent(this, HotelListActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "Reservation " + (position + 1) + " selected");
		// Send us to a specific room's screen
		Intent i = new Intent(this, RoomViewActivity.class);
		i.putExtra("start", results.get(position).getStart().toString("yyyy-MM-dd"));
		i.putExtra("end", results.get(position).getEnd().toString("yyyy-MM-dd"));
		i.putExtra("id", results.get(position).getRoomID());
		i.putExtra("resID", results.get(position).getResID());
		this.startActivity(i);
	}

	/**
	 * Searches for all the user's reservations
	 */
	private void search() {
		try {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Log.d(TAG, sp.getString("pref_user_name", "username"));
			results = dba.fetchReservations(sp.getString("pref_user_name", "username"));
			ReservationListAdapter adapter = new ReservationListAdapter(this, results);
			setListAdapter(adapter);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(this, "Failed to find reservations", Toast.LENGTH_LONG).show();
		}
	}
}
