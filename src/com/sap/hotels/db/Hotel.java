package com.sap.hotels.db;

/**
 * Hotel data representation
 * 
 * @author I838546
 * 
 */
public class Hotel {

	private String name, location, phone, email;
	private int id;

	public Hotel(int id, String name, String location, String phone, String email) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.phone = phone;
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Hotel() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
}
