package com.sap.hotels.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sap.hotels.R;
import com.sap.hotels.db.Amenity;

/**
 * Custom adapter for amenities
 * 
 * @author I838546
 * 
 */
public class AmenitiesListAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private ArrayList<Amenity> amenities;

	private final class ViewHolder {
		TextView name;
	}

	private ViewHolder m = null;
	private Context mContext;

	public AmenitiesListAdapter(Context context, ArrayList<Amenity> amenities) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.amenities = amenities;
	}

	@Override
	public int getCount() {
		return amenities.size();
	}

	@Override
	public Object getItem(int position) {
		return amenities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			m = new ViewHolder();
			convertView = mInflater.inflate(R.layout.amenity_row, null);
			convertView.setTag(m);
		} else {
			m = (ViewHolder) convertView.getTag();
		}

		m.name = (TextView) convertView.findViewById(R.id.amenityTextView);
		m.name.setText(amenities.get(position).getName());

		return convertView;
	}

}
