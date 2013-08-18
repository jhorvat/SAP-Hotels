package com.sap.hotels.db;

import org.joda.time.DateTime;

/**
 * Event representation
 * 
 * @author I838546
 * 
 */
public class Event {

	DateTime eventTime;
	int roomID, eventID;
	String name, description;

	public Event(int eventID, int roomID, DateTime eventTime, String name, String description) {
		this.eventTime = eventTime;
		this.roomID = roomID;
		this.eventID = eventID;
		this.name = name;
		this.description = description;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public int getEventID() {
		return eventID;
	}

	public void setEventID(int eventID) {
		this.eventID = eventID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return eventTime.toString("MM/dd/yyyy");
	}

	public String getTime() {
		return eventTime.toString("hh:mm aa");
	}
}
