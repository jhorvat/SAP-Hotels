package com.sap.hotels.ui;

import java.text.DecimalFormat;

import org.joda.time.LocalDate;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;
import com.sap.hotels.db.Room;
import com.sap.hotels.ui.adapters.AmenitiesListAdapter;

/**
 * Abstracted class for a room view. Alright! Following DRY principles
 * 
 * @author I838546
 * 
 */
public abstract class RoomInfoBaseActivity extends Activity {

	/**
	 * View holder pattern
	 * 
	 * @author I838546
	 * 
	 */
	protected class VH {
		TextView num, price, occupy, name, interval;
		ListView amenities;
	}

	private static final String TAG = "RoomInfoActivity";

	private VH m;
	protected LocalDate startT, endT;
	protected DBAccessor dba;
	protected int roomId;
	protected Room room;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResourceId());

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

		// Setup views
		m = new VH();
		m.num = (TextView) findViewById(R.id.roomNumberText);
		m.name = (TextView) findViewById(R.id.roomNameText);
		m.price = (TextView) findViewById(R.id.roomPriceText);
		m.occupy = (TextView) findViewById(R.id.roomPeopleText);
		m.interval = (TextView) findViewById(R.id.dateIntervalText);
		m.amenities = (ListView) findViewById(R.id.listView1);

		// Get the bundle
		Bundle args = getIntent().getExtras();
		roomId = args.getInt("id");
		startT = new LocalDate(args.getString("start"));
		endT = new LocalDate(args.getString("end"));

		// Get the room and amenities listing
		try {
			room = dba.fetchRoom(roomId);
			m.amenities.setAdapter(new AmenitiesListAdapter(this, dba.fetchAmenities(roomId)));
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(this, "Failed to retrieve room", Toast.LENGTH_LONG).show();
		}

		// Set our pretty looking text
		m.num.setText("Room #: " + room.getRoomNumber());
		m.name.setText(room.getName());
		m.price.setText(new DecimalFormat("$#.##/night").format(room.getPrice()));
		m.occupy.setText(String.valueOf(room.getMaxOccupy())
				+ ((room.getMaxOccupy() == 1) ? " person" : " people"));
		m.interval.setText(startT.toString("EEE, MMM dd") + " to " + endT.toString("EEE, MMM dd"));

		// The list is just there no need for the user to be able to click it
		m.amenities.setClickable(false);
	}

	/**
	 * This method is required to get the ID for the layout since it may be
	 * slightly different
	 * 
	 * @return The layout file's ID
	 */
	protected abstract int getLayoutResourceId();
}
