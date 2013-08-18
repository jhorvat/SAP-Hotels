package com.sap.hotels.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sap.hotels.R;
import com.sap.hotels.db.Event;

/**
 * Event listing adapter
 * 
 * @author I838546
 * 
 */
public class EventListAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private ArrayList<Event> events;

	private final class ViewHolder {
		TextView nameTime, date;
	}

	private ViewHolder m = null;
	private Context mContext;

	public EventListAdapter(Context context, ArrayList<Event> events) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.events = events;
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			m = new ViewHolder();
			convertView = mInflater.inflate(R.layout.event_row, null);
			convertView.setTag(m);
		} else {
			m = (ViewHolder) convertView.getTag();
		}

		m.nameTime = (TextView) convertView.findViewById(R.id.eventNameTimeText);
		m.date = (TextView) convertView.findViewById(R.id.dateText);

		m.nameTime.setText(events.get(position).getName() + " @ " + events.get(position).getTime());
		m.date.setText(events.get(position).getDate());

		return convertView;
	}
}
