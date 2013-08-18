package com.sap.hotels.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;
import com.sap.hotels.db.Room;
import com.sap.hotels.ui.adapters.RoomListAdapter;

/**
 * Activity to list the rooms that match the reservation search criteria
 * @author I838546
 *
 */
public class RoomListActivity extends ListActivity {

	private static final String TAG = "RoomListActivity";
	private DBAccessor dba;
	private ArrayList<Room> results;
	private String minPrice, maxPrice, minOccupy, maxOccupy, startTime, endTime, hotelID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_list);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get singleton instance
		dba = DBAccessor.getInstance();

		try {
			dba.open(this);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(RoomListActivity.this, "Failed to open db", Toast.LENGTH_LONG).show();
		}

		Bundle args = getIntent().getExtras();
		hotelID = args.getString("hotel_id");
		minPrice = args.getString("min_price");
		maxPrice = args.getString("max_price");
		minOccupy = args.getString("min_occupy");
		maxOccupy = args.getString("max_occupy");
		startTime = args.getString("start_time");
		endTime = args.getString("end_time");

		Log.d(TAG, "Got " + hotelID + " from parent");

		results = new ArrayList<Room>();
		search();
	}

	/**
	 * Runs searching the db
	 * 
	 */
	private void search() {
		// Populate the list with the results
		try {
			results = dba.roomSearch(minPrice, maxPrice, minOccupy, maxOccupy, startTime, endTime,
					hotelID);
			RoomListAdapter adapter = new RoomListAdapter(RoomListActivity.this, results);
			setListAdapter(adapter);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(RoomListActivity.this, "Search failed", Toast.LENGTH_LONG).show();
		}
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "Room #" + (position + 1) + " selected");
		// Send us to a specific room's screen
		Intent i = new Intent(RoomListActivity.this, ReserveRoomActivity.class);
		i.putExtra("start", startTime);
		i.putExtra("end", endTime);
		i.putExtra("id", results.get(position).getID());
		RoomListActivity.this.startActivity(i);
	}

}
