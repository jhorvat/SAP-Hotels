package com.sap.hotels.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sap.hotels.R;
import com.sap.hotels.db.Reservation;

/**
 * Custom adapter for reservation elements
 * 
 * @author I838546
 * 
 */
public class ReservationListAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private ArrayList<Reservation> reservations;

	private final class ViewHolder {
		TextView name, roomNumber, dateInterval;
	}

	private ViewHolder m = null;
	private Context mContext;

	public ReservationListAdapter(Context context, ArrayList<Reservation> reservations) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.reservations = reservations;
	}

	@Override
	public int getCount() {
		return reservations.size();
	}

	@Override
	public Object getItem(int position) {
		return reservations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			m = new ViewHolder();
			convertView = mInflater.inflate(R.layout.reservation_row, null);
			convertView.setTag(m);
		} else {
			m = (ViewHolder) convertView.getTag();
		}

		m.name = (TextView) convertView.findViewById(R.id.hotelLocationText);
		m.roomNumber = (TextView) convertView.findViewById(R.id.roomNumText);
		m.dateInterval = (TextView) convertView.findViewById(R.id.dateIntervalText);

		m.name.setText(reservations.get(position).getName());
		m.roomNumber.setText("Room " + String.valueOf(reservations.get(position).getRoomNum()));
		m.dateInterval.setText(reservations.get(position).getStart().toString("MM/dd/yy") + "-"
				+ reservations.get(position).getEnd().toString("MM/dd/yy"));

		return convertView;
	}
}
