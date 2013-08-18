package com.sap.hotels.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ianywhere.ultralitejni16.ConfigFileAndroid;
import com.ianywhere.ultralitejni16.Connection;
import com.ianywhere.ultralitejni16.DatabaseManager;
import com.ianywhere.ultralitejni16.PreparedStatement;
import com.ianywhere.ultralitejni16.ResultSet;
import com.ianywhere.ultralitejni16.StreamHTTPParms;
import com.ianywhere.ultralitejni16.SyncParms;
import com.ianywhere.ultralitejni16.ULjException;
import com.sap.hotels.R;

/**
 * Singleton database access since we only ever need one connection this ensures
 * that there aren't multiple connections lying around being unmanaged
 * 
 * @author I838546
 * 
 */
public class DBAccessor {

	private final static String TAG = "DBAccessor";
	public final static String HOTEL_ID = "_id_hotel";

	private Connection conn;
	private Context mContext;

	/**
	 * Thread-safe singleton holder
	 * 
	 * @author I838546
	 * 
	 */
	private static class SingletonHolder {
		/**
		 * This is the singleton accessor it is created on the first call of
		 * getInstance
		 */
		public static final DBAccessor INSTANCE = new DBAccessor();
	}

	/**
	 * Get the singleton instance
	 * 
	 * @return The singleton
	 */
	public static DBAccessor getInstance() {
		Log.d(TAG, "Instance requested");
		return SingletonHolder.INSTANCE;
	}

	private DBAccessor() {
		conn = null;
	}

	/**
	 * Initializes the connection to the database unfortunately there is no way
	 * to keep this connection constant
	 * 
	 * @param mContext
	 *            App context
	 * @throws ULjException
	 *             On failure to open
	 */
	public void open(Context context) throws ULjException {
		ConfigFileAndroid config = DatabaseManager.createConfigurationFileAndroid("custDB.udb",
				context);
		mContext = context;

		// If the connection exists erase it
		if (conn != null) {
			Log.d(TAG, "Releasing stale connection");
			conn.commit();
			conn.release();
		}

		// Try to connect to the database if that fails it doesn't exist so
		// create it
		try {
			conn = DatabaseManager.connect(config);
			Log.d(TAG, "Connected to database");
		} catch (ULjException e) {
			Log.e(TAG, e.getMessage());
			Log.d(TAG, "Creating a new database");
			conn = DatabaseManager.createDatabase(config);
			init();
		}
	}

	/**
	 * Loads a text file from the raw resources that contains the SQL schema
	 * script
	 * 
	 * @throws ULjException
	 *             On error
	 */
	private void init() throws ULjException {
		// Get a reader for the text file
		BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getResources()
				.openRawResource(R.raw.remote_setup)));
		String line;
		try {
			Log.d(TAG, "Creating new database schema");
			// Execute the SQL we read line by line
			while ((line = reader.readLine()) != null) {
				exec(line);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(mContext, "Failed to initialize db schema", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Syncs with the MobiLink server
	 * 
	 * @throws ULjException
	 */
	public void sync(String username, String pass) throws ULjException {
		SyncParms sp = conn.createSyncParms(SyncParms.HTTP_STREAM, username, "ver1");
		StreamHTTPParms stream = sp.getStreamParms();
		stream.setPort(2439);
		stream.setHost("10.7.170.186");
		sp.setPassword(pass);

		Log.d(TAG, "Attempting a sync with " + stream.getHost() + ":" + stream.getPort() + " as "
				+ username + " " + pass);
		conn.synchronize(sp);
		Log.d(TAG, "Sync with " + stream.getHost() + ":" + stream.getPort() + " successful");
	}

	/**
	 * Execute an sql line
	 * 
	 * @param sql
	 *            The sql
	 * @throws ULjException
	 *             On error
	 */
	private void exec(String sql) throws ULjException {
		Log.d(TAG, "Executing: " + sql + " on the db");
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.execute();
		ps.close();
		conn.commit();
	}

	/**
	 * Execute an sql query
	 * 
	 * @param sql
	 *            The query
	 * @return ResultSet of the query
	 * @throws ULjException
	 *             On error
	 */
	private ResultSet query(String sql) throws ULjException {
		Log.d(TAG, "Querying: " + sql + " on the db");
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		return rs;
	}

	/**
	 * Get an array list of every hotel for listing purposes
	 * 
	 * @return An array list of every hotel
	 * @throws ULjException
	 *             On error
	 */
	public ArrayList<Hotel> fetchAllHotels() throws ULjException {
		ArrayList<Hotel> data = new ArrayList<Hotel>();
		ResultSet rs = query("SELECT * FROM Hotels");

		while (rs.next())
			data.add(new Hotel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs
					.getString(5)));

		return data;
	}

	/**
	 * Returns an ArrayList of fuzzy search results for the hotel name
	 * 
	 * @param query
	 *            The hotel name to search for
	 * @return The ArrayList of results to be used by the view adapter
	 * @throws ULjException
	 *             On error
	 */
	public ArrayList<Hotel> hotelSearch(String query) throws ULjException {
		ArrayList<Hotel> data = new ArrayList<Hotel>();
		ResultSet rs = query("SELECT * FROM Hotels WHERE name LIKE '%" + query + "%'");

		while (rs.next())
			data.add(new Hotel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs
					.getString(5)));

		return data;
	}

	/**
	 * Get a specific hotel by id
	 * 
	 * @param n
	 *            The id
	 * @return The hotel
	 * @throws ULjException
	 *             On error
	 */
	public Hotel fetchHotel(int n) throws ULjException {
		ResultSet rs = query("SELECT * FROM Hotels WHERE \"Hotel ID\"='" + n + "'");
		return (rs.first()) ? new Hotel(rs.getInt(1), rs.getString(2), rs.getString(3),
				rs.getString(4), rs.getString(5)) : null;
	}

	/**
	 * Search for a room given all relevant criteria
	 * 
	 * @param minPrice
	 *            The minimum price per night
	 * @param maxPrice
	 *            The maximum price per night
	 * @param minOccupy
	 *            The minimum occupancy
	 * @param maxOccupy
	 *            The maximum occupancy
	 * @param startTime
	 *            The start date of the reservation
	 * @param endTime
	 *            The end date
	 * @return An ArrayList of all matching rooms
	 * @throws ULjException
	 *             On error
	 */
	public ArrayList<Room> roomSearch(String minPrice, String maxPrice, String minOccupy,
			String maxOccupy, String startTime, String endTime, String hotelID) throws ULjException {
		ArrayList<Room> data = new ArrayList<Room>();
		String query = "SELECT * FROM Rooms "
				+ "JOIN \"Room Type\" ON Rooms.\"Room Type ID\" = \"Room Type\".\"Room Type ID\" "
				+ "JOIN Rates ON \"Room Type\".\"Room Type ID\" = Rates.\"Room Type ID\" "
				+ "WHERE "
				+ "Rooms.\"Hotel ID\" = "
				+ hotelID
				+ " AND NOT EXISTS "
				+ "(SELECT * FROM Reservations WHERE "
				+ "Rooms.\"Room ID\" = Reservations.\"Room ID\" AND "
				+ "(DATE('"
				+ startTime
				+ "') < \"Start Date\" AND \"Start Date\" < DATE('"
				+ endTime
				+ "')) OR "
				+ "(DATE('"
				+ startTime
				+ "') < DATEADD(DD, \"Length of Stay\", \"Start Date\") AND DATEADD(DD, \"Length of Stay\", \"Start Date\") < DATE('"
				+ endTime + "'))) ";

		// If we have a value for minPrice
		if (!minPrice.equals(""))
			// Build the query differently if we also have a value for maxPrice
			// as well
			query += "AND Price "
					+ ((!maxPrice.equals("")) ? "BETWEEN " + minPrice + " AND " + maxPrice + " "
							: ">= " + minPrice) + " ";
		// If we only have a value for maxPrice
		else if (!maxPrice.equals(""))
			query += "AND Price <= " + maxPrice + " ";

		// If we have a value for minOccupy
		if (!minOccupy.equals(""))
			// Build the query differently if we also have a value for maxOccuy
			// as well
			query += "AND \"Max Occupy\" "
					+ ((!maxOccupy.equals("")) ? "BETWEEN " + minOccupy + " AND " + maxOccupy + " "
							: ">= " + minOccupy) + " ";
		// If we only have a value for maxOccupy
		else if (!maxOccupy.equals(""))
			query += "AND \"Max Occupy\" <= " + maxOccupy + " ";

		ResultSet rs = query(query);

		while (rs.next())
			data.add(new Room(rs.getInt("Room ID"), rs.getInt("Room Number"), rs
					.getInt("Max Occupy"), rs.getDouble("Price"), rs.getString("Name")));

		return data;
	}

	/**
	 * Retrieve a room given its ID
	 * 
	 * @param roomId
	 *            the room's ID
	 * @return A new room object
	 * @throws ULjException
	 *             On error
	 */
	public Room fetchRoom(int roomId) throws ULjException {
		ResultSet rs = query("SELECT * FROM Rooms "
				+ "JOIN \"Room Type\" ON Rooms.\"Room Type ID\" = \"Room Type\".\"Room Type ID\" "
				+ "LEFT OUTER JOIN Rates ON \"Room Type\".\"Room Type ID\" = Rates.\"Room Type ID\" "
				+ "WHERE \"Room ID\"=" + roomId);
		return (rs.first()) ? new Room(rs.getInt("Room ID"), rs.getInt("Room Number"),
				rs.getInt("Max Occupy"), rs.getDouble("Price"), rs.getString("Name")) : null;
	}

	/**
	 * Inserts a reservation into the reservation table
	 * 
	 * @param custID
	 *            The customer's ID number
	 * @param roomID
	 *            The room's ID
	 * @param start
	 *            The start date of the reservation
	 * @param end
	 *            The end date of the reservation
	 * @throws ULjException
	 *             On error
	 */
	public void addReservation(int custID, int roomID, LocalDate start, LocalDate end)
			throws ULjException {
		TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		exec("INSERT INTO Reservations(\"Room ID\", \"Customer ID\", \"Start Date\", \"Length of Stay\") VALUES("
				+ roomID
				+ ","
				+ custID
				+ ",'"
				+ start.toString("yyyy-MM-dd")
				+ "',"
				+ Days.daysBetween(start, end).getDays() + ")");
		Toast.makeText(mContext, "Reserving...", Toast.LENGTH_SHORT).show();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext
				.getApplicationContext());
		sync(prefs.getString("pref_user_name", ""), prefs.getString("pref_password", ""));
		Toast.makeText(mContext, "Reservation made!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Retrieves all reservations for a specified user
	 * 
	 * @param custEmail
	 *            The customer's email, this will eventually be pulled from
	 *            SharedPreferences
	 * @return The ArrayList of Reservations
	 * @throws ULjException
	 *             On error
	 */
	public ArrayList<Reservation> fetchReservations(String custEmail) throws ULjException {
		ResultSet rs = query("SELECT \"Reservation ID\", Rooms.\"Room ID\", \"Start Date\", \"Length of Stay\", \"Room Number\", Hotels.Name, Location FROM Rooms "
				+ "JOIN "
				+ "Reservations ON Rooms.\"Room ID\" = Reservations.\"Room ID\" "
				+ "JOIN "
				+ "Hotels ON Rooms.\"Hotel ID\" = Hotels.\"Hotel ID\" "
				+ "WHERE \"Customer ID\" = (SELECT \"Customer ID\" FROM Customers WHERE email='"
				+ custEmail
				+ "')"
				+ "AND "
				+ "\"Start Date\" > '"
				+ new LocalDate().toString("yyyy-MM-dd") + "'");
		ArrayList<Reservation> data = new ArrayList<Reservation>();

		while (rs.next()) {
			LocalDate start = new LocalDate(rs.getString("Start Date"));
			data.add(new Reservation(rs.getInt("Reservation ID"), start, start.plusDays(rs
					.getInt("Length of Stay")), rs.getInt("Room ID"), rs.getInt("Room Number"), rs
					.getString("Name") + ", " + rs.getString("Location")));
		}

		return data;
	}

	/**
	 * Removes a reservation
	 * 
	 * @param resID
	 *            The reservation ID
	 * @throws ULjException
	 *             On error
	 */
	public void removeReservation(int resID) throws ULjException {
		Toast.makeText(mContext, "Removing reservation...", Toast.LENGTH_SHORT).show();
		exec("DELETE FROM Reservations WHERE \"Reservation ID\"=" + resID);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext
				.getApplicationContext());
		sync(prefs.getString("pref_user_name", ""), prefs.getString("pref_password", ""));
		Toast.makeText(mContext, "Reservation removed!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Fetches all the amenities attached to the room the query will know what
	 * room type it is
	 * 
	 * @param roomID
	 *            the room's ID
	 * @return ArrayList of amenities in the room
	 * @throws ULjException
	 *             ON error
	 */
	public ArrayList<Amenity> fetchAmenities(int roomID) throws ULjException {
		ResultSet rs = query("SELECT \"Room Type\".Name, Rooms.\"Room Number\", Hotels.Name, Hotels.Location, \"Amenity Types\".Description "
				+ "FROM Rooms "
				+ "JOIN \"Room Type\" ON \"Room Type\".\"Room Type ID\" = Rooms.\"Room Type ID\" "
				+ "JOIN Amenities ON \"Room Type\".\"Room Type ID\" = Amenities.\"Room Type ID\" "
				+ "JOIN \"Amenity Types\" ON \"Amenity Types\".\"Amenity ID\" = Amenities.\"Amenity ID\" AND Amenities.\"Room Type ID\" = \"Room Type\".\"Room Type ID\" "
				+ "JOIN Hotels ON Hotels.\"Hotel ID\" = Rooms.\"Hotel ID\" "
				+ "WHERE Rooms.\"Room ID\" = " + roomID);
		ArrayList<Amenity> data = new ArrayList<Amenity>();

		while (rs.next())
			data.add(new Amenity(rs.getString("Description")));

		return data;
	}

	/**
	 * Retrieves the next 3 months worth of events from the hotel
	 * 
	 * @param hotelID
	 *            The hotel to lookup
	 * @return ArrayList of events in the hotel
	 * @throws ULjException
	 *             On error
	 */
	public ArrayList<Event> fetchEvents(int hotelID) throws ULjException {
		ResultSet rs = query("SELECT \"Event ID\", Events.\"Room ID\", \"Event Time\", Events.Name, Description FROM EVENTS "
				+ "JOIN Rooms ON Events.\"Room ID\" = Rooms.\"Room ID\" "
				+ "JOIN Hotels ON Rooms.\"Hotel ID\" = Hotels.\"Hotel ID\" "
				+ "WHERE "
				+ "Hotels.\"Hotel ID\" = "
				+ hotelID
				+ " "
				+ "AND "
				+ "NOT \"Event Time\" < CURRENT DATE "
				+ "AND "
				+ "NOT \"Event Time\" > DATEADD(MM, 3, CURRENT DATE)");
		ArrayList<Event> data = new ArrayList<Event>();
		// Setup the datetime pattern we're going to get from the db
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

		while (rs.next())
			data.add(new Event(rs.getInt("Event ID"), rs.getInt("Room ID"), format.parseDateTime(rs
					.getString("Event Time")), rs.getString("Name"), rs.getString("Description")));

		return data;
	}

	/**
	 * Determines if a user is present in the customer's table
	 * 
	 * @param username
	 *            The user's email
	 * @return True if present false otherwise
	 * @throws ULjException
	 *             On error
	 */
	public boolean userExists(String username) throws ULjException {
		return query("SELECT * FROM Customers WHERE email='" + username + "'").next();
	}

	/**
	 * Gets a customer's ID
	 * 
	 * @param username
	 *            Customer's user name
	 * @return Customer ID
	 * @throws ULjException
	 *             On error
	 */
	public int getCustId(String username) throws ULjException {
		ResultSet rs = query("SELECT \"Customer ID\" FROM Customers WHERE email='" + username + "'");
		return (rs.first()) ? rs.getInt("Customer ID") : -1;
	}
}
