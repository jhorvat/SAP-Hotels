package com.sap.hotels.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;
import com.sap.hotels.db.Hotel;
import com.sap.hotels.ui.adapters.HotelListAdapter;

/**
 * Lists all hotel in the db
 * @author I838546
 *
 */
public class HotelListActivity extends ListActivity {

	private static final String TAG = "HotelListActivity";
	private ArrayList<Hotel> data;
	private DBAccessor dba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_list);

		registerForContextMenu(getListView());
	}

	/**
	 * Refreshes the hotel list when the activity comes to the foreground
	 */
	protected void onStart() {
		super.onStart();
		// Init the dba
		dba = DBAccessor.getInstance();
		new SyncList().execute();
	}

	/**
	 * Fill the list view
	 */
	private void fillData() {
		try {
			Log.d(TAG, "Filling ListView");
			data = dba.fetchAllHotels();
			HotelListAdapter adapter = new HotelListAdapter(HotelListActivity.this, data);
			setListAdapter(adapter);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(HotelListActivity.this, "Failed to load hotel list", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hotel_list, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.action_search:
			Log.d(TAG, "Search button pushed");
			i = new Intent(HotelListActivity.this, HotelSearchActivity.class);
			HotelListActivity.this.startActivity(i);
			return true;
			// Manual sync
		case R.id.action_sync:
			Log.d(TAG, "Sync button pushed");
			new SyncList().execute();
			return true;
			// Settings button
		case R.id.action_settings:
			i = new Intent(HotelListActivity.this, SettingsActivity.class);
			HotelListActivity.this.startActivity(i);
			return true;
		case R.id.action_ct_reservations:
			i = new Intent(this, ReservationListActivity.class);
			this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "Hotel #" + (position + 1) + " selected");
		// Send us to a specific hotel's screen
		Intent i = new Intent(HotelListActivity.this, HotelViewActivity.class);
		i.putExtra("class", getIntent().getComponent().getClassName());
		i.putExtra(DBAccessor.HOTEL_ID, data.get(position).getID());
		HotelListActivity.this.startActivity(i);
	}

	/**
	 * Async MobiLink synchronization
	 * 
	 * @author I838546
	 * 
	 */
	private class SyncList extends AsyncTask<Void, ULjException, Void> {

		private static final String TAG = "SyncList";

		private final class ViewHolder {
			LinearLayout progress;
			LinearLayout list;
		}

		private ViewHolder m;

		/**
		 * Setup everything
		 */
		protected void onPreExecute() {
			Log.d(TAG, "Preparing ASyncTask");
			m = new ViewHolder();
			m.progress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
			m.list = (LinearLayout) findViewById(R.id.listContainer);

			m.list.setVisibility(View.INVISIBLE);
			m.progress.setVisibility(View.VISIBLE);
		}

		/**
		 * Async execution
		 */
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Log.d(TAG, "Syncing list in background");
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(HotelListActivity.this);
				dba.open(HotelListActivity.this);

				dba.sync(prefs.getString("pref_user_name", ""),
						prefs.getString("pref_password", ""));
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
			Toast.makeText(HotelListActivity.this, "MobiLink sync failed", Toast.LENGTH_LONG)
					.show();
		}

		/**
		 * Finish up
		 */
		protected void onPostExecute(Void result) {
			Log.d(TAG, "ASyncTask completed, cleaning up and posting data");
			fillData();
			m.list.setVisibility(View.VISIBLE);
			m.progress.setVisibility(View.INVISIBLE);
		}
	}
}
