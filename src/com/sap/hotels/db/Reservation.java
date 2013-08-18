package com.sap.hotels.db;

import org.joda.time.LocalDate;

/**
 * Reservation data holder
 * 
 * @author I838546
 * 
 */
public class Reservation {

	private LocalDate start, end;
	private int roomID, roomNum, resID;

	public int getResID() {
		return resID;
	}

	public void setResID(int resID) {
		this.resID = resID;
	}

	private String name;

	public Reservation(int resID, LocalDate start, LocalDate end, int roomID, int roomNum,
			String name) {
		this.resID = resID;
		this.start = start;
		this.end = end;
		this.roomID = roomID;
		this.roomNum = roomNum;
		this.name = name;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
