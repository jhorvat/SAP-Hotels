package com.sap.hotels.ui.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.sap.hotels.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sap.hotels.db.Room;

/**
 * Custom adapter for room elements
 * 
 * @author I838546
 * 
 */
public class RoomListAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private ArrayList<Room> rooms;

	private final class ViewHolder {
		TextView name, roomNumber, occupancy, price;
	}

	private ViewHolder m = null;
	private Context mContext;

	public RoomListAdapter(Context context, ArrayList<Room> rooms) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.rooms = rooms;
	}

	@Override
	public int getCount() {
		return rooms.size();
	}

	@Override
	public Object getItem(int position) {
		return rooms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			m = new ViewHolder();
			convertView = mInflater.inflate(R.layout.room_row, null);
			convertView.setTag(m);
		} else {
			m = (ViewHolder) convertView.getTag();
		}

		m.name = (TextView) convertView.findViewById(R.id.roomNameText);
		m.roomNumber = (TextView) convertView.findViewById(R.id.roomNumText);
		m.occupancy = (TextView) convertView.findViewById(R.id.roomOccupyText);
		m.price = (TextView) convertView.findViewById(R.id.roomPriceText);

		m.name.setText(rooms.get(position).getName());
		m.roomNumber.setText(String.valueOf(rooms.get(position).getRoomNumber()));
		m.occupancy.setText(String.valueOf(rooms.get(position).getMaxOccupy())
				+ ((rooms.get(position).getMaxOccupy() == 1) ? " person" : " people"));
		m.price.setText(new DecimalFormat("$#.##/night").format(rooms.get(position).getPrice()));

		return convertView;

	}
}
