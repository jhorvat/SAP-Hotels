package com.sap.hotels.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sap.hotels.R;
import com.sap.hotels.db.Hotel;

/**
 * Custom list adapter for hotels
 * 
 * @author I838546
 * 
 */
public class HotelListAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private ArrayList<Hotel> hotels;

	private final class ViewHolder {
		TextView nameTextView;
		TextView addressTextView;
	}

	private ViewHolder mHolder = null;
	private Context mContext;

	public HotelListAdapter(Context context, ArrayList<Hotel> hotels) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.hotels = hotels;
	}

	@Override
	public int getCount() {
		return hotels.size();
	}

	@Override
	public Object getItem(int position) {
		return hotels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.hotel_row, null);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textView);
		mHolder.nameTextView.setText(hotels.get(position).getName());
		mHolder.addressTextView = (TextView) convertView.findViewById(R.id.address_textView);
		mHolder.addressTextView.setText(hotels.get(position).getLocation());
		return convertView;

	}
}
