package com.chrisb.coronavirustracker.data;

public class LocationStatsDTO {

    private String state;

    private String country;

    private int lastUpdatedValue;

    private int newData;

    public LocationStatsDTO(){}

    public LocationStatsDTO(String state, String country, int lastUpdatedValue, int newData) {
	this.state = state;
	this.country = country;
	this.lastUpdatedValue = lastUpdatedValue;
	this.newData = newData;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public int getLastUpdatedValue() {
	return lastUpdatedValue;
    }

    public void setLastUpdatedValue(int lastUpdatedValue) {
	this.lastUpdatedValue = lastUpdatedValue;
    }

    public int getNewData() {
	return newData;
    }

    public void setNewData(int newData) {
	this.newData = newData;
    }

    @Override
    public String toString() {
	return "LocationStatsDTO{" +
	    "state='" + state + '\'' +
	    ", country='" + country + '\'' +
	    ", lastUpdatedValue=" + lastUpdatedValue +
	    ", newData=" + newData +
	    '}';
    }
}
