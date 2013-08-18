package com.sap.hotels.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;

/**
 * Reserved view of a room that has been reserved by the user
 * 
 * @author I838546
 * 
 */
public class RoomViewActivity extends RoomInfoBaseActivity {

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

		final Bundle args = getIntent().getExtras();

		// Setup cancel reservation button
		m = new ViewHolder();
		m.reserve = (Button) findViewById(R.id.reserveButton);
		m.reserve.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					dba.removeReservation(args.getInt("resID"));
					m.reserve.setText("Cancelled");
					m.reserve.setEnabled(false);
				} catch (ULjException e) {
					Log.e(TAG, e.getMessage());
					Toast.makeText(arg0.getContext(), "Failed to remove reservation",
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
			Intent i = new Intent(this, ReservationListActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method points the base class to the layout file to load
	 */
	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_room_view;
	}

}
