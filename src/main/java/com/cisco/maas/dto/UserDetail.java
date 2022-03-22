package com.cisco.maas.dto;
/**
 * This class contains user details and its setters and getters.
  */
public class UserDetail {

	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserDetail [id=" + id + ", name=" + name + "]";
	}
}
