package com.sap.hotels.db;

/**
 * Hotel room representation
 * 
 * @author I838546
 * 
 */
public class Room {

	private int ID, roomNumber, maxOccupy;
	private double price;
	private String name;

	public Room(int ID, int roomNumber, int maxOccupy, double price, String name) {
		this.ID = ID;
		this.roomNumber = roomNumber;
		this.maxOccupy = maxOccupy;
		this.price = price;
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public int getMaxOccupy() {
		return maxOccupy;
	}

	public void setMaxOccupy(int maxOccupy) {
		this.maxOccupy = maxOccupy;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
