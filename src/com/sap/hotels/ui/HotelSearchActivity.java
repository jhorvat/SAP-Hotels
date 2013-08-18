package com.sap.hotels.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;
import com.sap.hotels.db.Hotel;
import com.sap.hotels.ui.adapters.HotelListAdapter;

/**
 * The search activity, opens SearchView Action and gets focus by default.
 * Includes predictive typing
 * 
 * @author I838546
 * 
 */
public class HotelSearchActivity extends ListActivity {

	private static final String TAG = "HotelSearchActivity";
	private DBAccessor dba;
	private ArrayList<Hotel> results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get singleton instance
		dba = DBAccessor.getInstance();

		Intent i = getIntent();
		if (Intent.ACTION_SEARCH.equals(i.getAction())) {
			String query = i.getStringExtra(SearchManager.QUERY);
			search(query);
		} else {
			search("");
		}
	}

	/**
	 * Runs searching the db
	 * 
	 * @param query
	 *            The query string
	 */
	private void search(String query) {
		// Populate the list with the results
		try {
			Log.d(TAG, "Searching for " + query);
			results = dba.hotelSearch(query);
			HotelListAdapter adapter = new HotelListAdapter(HotelSearchActivity.this, results);
			setListAdapter(adapter);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(HotelSearchActivity.this, "Search failed", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);

		// Expand the search view
		MenuItem sv = menu.findItem(R.id.action_search);
		sv.expandActionView();

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String arg0) {
				return onQueryTextSubmit(arg0);
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				search(arg0);
				return true;
			}

		});
		searchView.requestFocus();
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Send us to a specific hotel's screen
		Log.d(TAG, "Hotel " + (position + 1) + " clicked");
		Intent i = new Intent(HotelSearchActivity.this, HotelViewActivity.class);
		i.putExtra("class", getIntent().getComponent().getClassName());
		i.putExtra(DBAccessor.HOTEL_ID, results.get(position).getID());
		HotelSearchActivity.this.startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			i = new Intent(HotelSearchActivity.this, HotelListActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			HotelSearchActivity.this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
