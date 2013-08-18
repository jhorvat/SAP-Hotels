package com.sap.hotels.ui;

import java.util.ArrayList;
import java.util.Locale;

import org.joda.time.LocalDate;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;
import com.sap.hotels.db.DBAccessor;
import com.sap.hotels.db.Event;
import com.sap.hotels.db.Hotel;
import com.sap.hotels.db.Room;
import com.sap.hotels.ui.adapters.EventListAdapter;

/**
 * Detailed view of a hotel
 * @author I838546
 *
 */
public class HotelViewActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static final String TAG = "HotelViewActivity";

	// Expected values in bundle
	public static final String ARG_HOTEL_ID = "hotel_id";
	public static final String ARG_HOTEL_NAME = "hotel_name";
	public static final String ARG_HOTEL_LOCATION = "hotel_location";
	public static final String ARG_HOTEL_PHONE = "hotel_phone";
	public static final String ARG_HOTEL_EMAIL = "hotel_email";

	private DBAccessor dba;
	private Integer HOTEL_ID;
	private Class parent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_view);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// Setup db access
		dba = DBAccessor.getInstance();
		try {
			dba.open(this);
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(HotelViewActivity.this, "Failed to open db", Toast.LENGTH_LONG).show();
		}

		// Get the hotel we're looking at from the intent
		HOTEL_ID = (savedInstanceState == null) ? null : (Integer) savedInstanceState
				.getSerializable(DBAccessor.HOTEL_ID);
		if (HOTEL_ID == null) {
			Bundle extras = getIntent().getExtras();
			HOTEL_ID = extras != null ? extras.getInt(DBAccessor.HOTEL_ID) : null;
		}

		// Get the class name from the launching intent which specifies the
		// activity to return to with 'up'
		String className = (savedInstanceState == null) ? null : (String) savedInstanceState
				.getSerializable("class");
		if (className == null) {
			Bundle extras = getIntent().getExtras();
			className = extras != null ? extras.getString("class") : null;
			if (className != null && !className.equals("")) {
				try {
					parent = Class.forName(className);
				} catch (ClassNotFoundException e) {
					Log.e(TAG, e.getMessage());
					Toast.makeText(this, "Failed to load parent class", Toast.LENGTH_LONG).show();
				}
			}
		}

		Log.d(TAG, "Hotel view launched for Hotel #" + HOTEL_ID + " from " + className);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hotel_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		// Open settings
		case R.id.action_settings:
			i = new Intent(HotelViewActivity.this, SettingsActivity.class);
			HotelViewActivity.this.startActivity(i);
			return true;
		case android.R.id.home:
			// app icon in action bar clicked; go home
			i = new Intent(HotelViewActivity.this, parent);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			HotelViewActivity.this.startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment frag = new Fragment();
			switch (position) {
			// Summary tab
			case 0:
				frag = new SummarySectionFragment();
				frag.setArguments(hotelSummaryInfo());
				break;
			case 1:
				frag = new ReserveSectionFragment();
				frag.setArguments(hotelSummaryInfo());
				break;
			case 2:
				frag = new EventsSectionFragment();
				frag.setArguments(hotelSummaryInfo());
				break;
			}

			return frag;
		}

		/**
		 * Get a bundle for the summary page
		 * 
		 * @return The summary's bundle
		 */
		private Bundle hotelSummaryInfo() {
			Hotel h;
			Bundle b = new Bundle();
			try {
				h = dba.fetchHotel(HOTEL_ID);
				b.putInt(ARG_HOTEL_ID, HOTEL_ID);
				b.putString(ARG_HOTEL_NAME, h.getName());
				b.putString(ARG_HOTEL_LOCATION, h.getLocation());
				b.putString(ARG_HOTEL_PHONE, h.getPhone());
				b.putString(ARG_HOTEL_EMAIL, h.getEmail());
			} catch (ULjException e) {
				Log.e(TAG, e.getMessage());
				Toast.makeText(HotelViewActivity.this, "Failed to load hotel data from db",
						Toast.LENGTH_LONG).show();
			}
			return b;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.hotel_view_title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.hotel_view_title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.hotel_view_title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * Summary fragment
	 */
	public static class SummarySectionFragment extends Fragment {

		/**
		 * View holder pattern
		 * 
		 * @author I838546
		 * 
		 */
		private final class ViewHolder {
			TextView name, location, phone, email;
		}

		private ViewHolder m;

		public SummarySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater
					.inflate(R.layout.fragment_hotel_view_summary, container, false);

			// Get our views
			m = new ViewHolder();
			m.name = (TextView) rootView.findViewById(R.id.text_hotel_name);
			m.location = (TextView) rootView.findViewById(R.id.text_hotel_location);
			m.phone = (TextView) rootView.findViewById(R.id.text_hotel_phone);
			m.email = (TextView) rootView.findViewById(R.id.text_hotel_email);

			// Set values
			m.name.setText(getArguments().getString(ARG_HOTEL_NAME));
			m.location.setText(getArguments().getString(ARG_HOTEL_LOCATION));
			m.phone.setText(getArguments().getString(ARG_HOTEL_PHONE));
			m.email.setText(getArguments().getString(ARG_HOTEL_EMAIL));

			return rootView;
		}
	}

	/**
	 * Rooms listing fragment TODO: Add actual rooms and a ListViewAdapter once
	 * we're hooked up to the server
	 * 
	 * @author I838546
	 * 
	 */
	public static class ReserveSectionFragment extends Fragment implements OnClickListener {
		/**
		 * View holder pattern
		 * 
		 * @author I838546
		 * 
		 */
		private final class ViewHolder {
			EditText minPrice, maxPrice, minOccupy, maxOccupy;
			Button startTime, endTime, search;
		}

		/**
		 * Date picker for the start time
		 * 
		 * @author I838546
		 * 
		 */
		private class StartPickerFragment extends DialogFragment implements
				DatePickerDialog.OnDateSetListener {

			private static final String TAG = "StartPickerFragment";

			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// Initialize the picker to the current date
				int year = start.year().get();
				int month = start.monthOfYear().get() - 1;
				int day = start.dayOfMonth().get();

				return new DatePickerDialog(getActivity(), this, year, month, day);
			}

			/**
			 * On result set the start global and set the button text to the new
			 * date
			 */
			@Override
			public void onDateSet(DatePicker v, int year, int month, int day) {
				Log.d(TAG, "Start date set to " + (month + 1) + "/" + day + "/" + year);
				start = new LocalDate(year, month + 1, day);
				m.startTime.setText(start.toString("MM/dd/yyyy"));

				// Adjust the end time to be 1 day after the start date if its
				// earlier than the new start date
				if (end.compareTo(start) < 0) {
					end = start.plusDays(1);

					Log.d(TAG, "End reset to one day ahead, now " + end.toString("MM/dd/yyyy"));

					m.endTime.setText(end.toString("MM/dd/yyyy"));
				}

			}
		}

		/**
		 * Date picker for the end time
		 * 
		 * @author I838546
		 * 
		 */
		private class EndPickerFragment extends DialogFragment implements
				DatePickerDialog.OnDateSetListener {

			private static final String TAG = "EndPickerFragment";

			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// Initialize the picker to the current date
				int year = end.year().get();
				int month = end.monthOfYear().get() - 1;
				int day = end.dayOfMonth().get();

				return new DatePickerDialog(getActivity(), this, year, month, day);
			}

			/**
			 * On result set the end global and set the button to the new date
			 */
			@Override
			public void onDateSet(DatePicker v, int year, int month, int day) {
				Log.d(TAG, "End date set to " + year + "/" + (month + 1) + "/" + day);

				end = new LocalDate(year, month + 1, day);
				m.endTime.setText(end.toString("MM/dd/yyyy"));
			}
		}

		private static final String TAG = "ReserveSectionFragment";
		private ViewHolder m;
		private LocalDate start, end;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater
					.inflate(R.layout.fragment_hotel_view_reserve, container, false);

			// Initialize all our views
			m = new ViewHolder();
			m.minPrice = (EditText) rootView.findViewById(R.id.minPriceField);
			m.maxPrice = (EditText) rootView.findViewById(R.id.maxPriceField);
			m.minOccupy = (EditText) rootView.findViewById(R.id.minOccupyField);
			m.maxOccupy = (EditText) rootView.findViewById(R.id.maxOccupyField);
			m.startTime = (Button) rootView.findViewById(R.id.startTimeButton);
			m.endTime = (Button) rootView.findViewById(R.id.endTimeButton);
			m.search = (Button) rootView.findViewById(R.id.searchButton);

			m.startTime.setOnClickListener(this);
			m.endTime.setOnClickListener(this);
			m.search.setOnClickListener(this);

			// Initialize our calendars
			start = new LocalDate();
			end = new LocalDate();

			m.startTime.setText(start.toString("MM/dd/yyyy"));
			m.endTime.setText(end.toString("MM/dd/yyyy"));

			return rootView;
		}

		@Override
		public void onClick(View v) {
			DialogFragment frag;

			switch (v.getId()) {
			// Launch the start date picker
			case R.id.startTimeButton:
				Log.d(TAG, "Start time button clicked");
				frag = new StartPickerFragment();
				frag.show(getActivity().getFragmentManager(), "Start Date");
				break;
			// Launch the end date picker
			case R.id.endTimeButton:
				Log.d(TAG, "End time button clicked");
				frag = new EndPickerFragment();
				frag.show(getActivity().getFragmentManager(), "End Date");
				break;
			// Search button pressed
			case R.id.searchButton:
				Log.d(TAG, "Reservation search button clicked");
				Log.d(TAG,
						"Price range: $" + m.minPrice.getText() + " - $" + m.maxPrice.getText()
								+ "\nOccupancy range: " + m.minOccupy.getText() + " - "
								+ m.maxOccupy.getText() + "\nDate range: "
								+ start.toString("MM/dd/yyyy") + " - " + end.toString("MM/dd/yyyy"));

				if (validQuery()) {
					Intent i = new Intent(this.getActivity(), RoomListActivity.class);
					i.putExtra("min_price", m.minPrice.getText().toString());
					i.putExtra("max_price", m.maxPrice.getText().toString());
					i.putExtra("min_occupy", m.minOccupy.getText().toString());
					i.putExtra("max_occupy", m.maxOccupy.getText().toString());
					i.putExtra("start_time", start.toString("yyyy-MM-dd"));
					i.putExtra("end_time", end.toString("yyyy-MM-dd"));
					i.putExtra("hotel_id", String.valueOf(getArguments().getInt(ARG_HOTEL_ID)));
					this.getActivity().startActivity(i);
				}
				break;
			}
		}

		private boolean validQuery() {
			if (!m.minPrice.getText().toString().equals("")
					&& !m.maxPrice.getText().toString().equals("")) {
				if (Integer.valueOf(m.minPrice.getText().toString()) > Integer.valueOf(m.maxPrice
						.getText().toString())) {
					Toast.makeText(this.getActivity(), "Invalid price interval", Toast.LENGTH_LONG)
							.show();
					return false;
				}
			}

			if (!m.minOccupy.getText().toString().equals("")
					&& !m.maxOccupy.getText().toString().equals("")) {
				if (Integer.valueOf(m.minOccupy.getText().toString()) > Integer.valueOf(m.maxOccupy
						.getText().toString())) {
					Toast.makeText(this.getActivity(), "Invalid occupancy interval",
							Toast.LENGTH_LONG).show();
					return false;
				}
			}

			if (start.compareTo(end) > 0) {
				Toast.makeText(this.getActivity(), "Invalid date interval", Toast.LENGTH_LONG)
						.show();
				return false;
			}

			return true;
		}
	}

	/**
	 * Event listing fragment
	 * 
	 * @author I838546
	 * 
	 */
	public static class EventsSectionFragment extends Fragment {
		/**
		 * View holder pattern
		 * 
		 * @author I838546
		 * 
		 */
		private final class ViewHolder {
			ListView events;
			TextView empty;
		}

		/**
		 * Event information dialog
		 * 
		 * @author I838546
		 * 
		 */
		private class EventDialog extends DialogFragment {

			/**
			 * View holder pattern
			 * 
			 * @author I838546
			 * 
			 */
			private final class ViewHolder {
				TextView title, dateTime, location, description;
			}

			private ViewHolder m;

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// Get the builder
				AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

				// Get the custom dialog layout
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View v = inflater.inflate(R.layout.event_dialog, null);

				// Set the view, and the close button
				builder.setView(v).setPositiveButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							}
						});

				// Setup the textviews
				m = new ViewHolder();
				m.title = (TextView) v.findViewById(R.id.titleText);
				m.dateTime = (TextView) v.findViewById(R.id.dateTimeText);
				m.description = (TextView) v.findViewById(R.id.descText);
				m.location = (TextView) v.findViewById(R.id.locationText);

				m.title.setText(getArguments().getString("event_name"));
				m.dateTime.setText(getArguments().getString("event_date"));
				m.location.setText(getArguments().getString("event_location"));
				m.description.setText(getArguments().getString("event_desc"));

				return builder.create();
			}
		}

		private static final String TAG = "EventSectionFragment";

		private ArrayList<Event> results;
		private int hotelID;
		private ViewHolder m;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_hotel_view_events, container, false);

			hotelID = getArguments().getInt("hotel_id");

			// Setup db access
			final DBAccessor dba = DBAccessor.getInstance();
			try {
				dba.open(this.getActivity());
			} catch (ULjException e) {
				Log.e(TAG, e.getMessage());
				Toast.makeText(this.getActivity(), "Failed to open db", Toast.LENGTH_LONG).show();
			}

			m = new ViewHolder();
			m.empty = (TextView) rootView.findViewById(R.id.emptyText);
			m.events = (ListView) rootView.findViewById(R.id.eventList);

			try {
				// Get the events in the hotel that are coming up
				results = dba.fetchEvents(hotelID);
				// Setup the ListView
				EventListAdapter adapter = new EventListAdapter(this.getActivity(), results);
				m.events.setAdapter(adapter);

				if (adapter.getCount() != 0)
					m.empty.setVisibility(View.INVISIBLE);
			} catch (ULjException e) {
				Log.e(TAG, e.getMessage());
				Toast.makeText(this.getActivity(), "Failed to get events", Toast.LENGTH_LONG)
						.show();
			}

			// Bind the list click
			m.events.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
					try {
						// Get the room information
						Room location = dba.fetchRoom(results.get(pos).getRoomID());

						// Package all the event info
						Bundle args = new Bundle();
						args.putString("event_name", results.get(pos).getName());
						args.putString("event_date", results.get(pos).getDate() + " @ "
								+ results.get(pos).getTime());
						args.putString("event_desc", results.get(pos).getDescription());
						args.putString("event_location", location.getName() + ", Room #: "
								+ location.getRoomNumber());

						// Launch the event info dialog
						DialogFragment d = new EventDialog();
						d.setArguments(args);
						d.show(getActivity().getFragmentManager(), "Event Info");

					} catch (ULjException e) {
						Log.e(TAG, e.getMessage());
						Toast.makeText(view.getContext(), "Failed to get events", Toast.LENGTH_LONG)
								.show();
					}
				}
			});

			return rootView;
		}
	}
}
