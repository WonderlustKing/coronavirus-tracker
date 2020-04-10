package com.chrisb.coronavirustracker.data;

import java.util.HashMap;
import java.util.Map;

public class LocationStatsDTO {

    private String id;

    private String state;

    private String country;

    private int lastUpdatedValue;

    private int newData;

    private Map<String, Integer> dateCases = new HashMap<>();

    public LocationStatsDTO(){}

    public LocationStatsDTO(String id, String state, String country, int lastUpdatedValue, int newData, Map<String, Integer> dateCases) {
	this.id = id;
        this.state = state;
	this.country = country;
	this.lastUpdatedValue = lastUpdatedValue;
	this.newData = newData;
	this.dateCases = dateCases;
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

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Map<String, Integer> getDateCases() {
	return dateCases;
    }

    public void setDateCases(Map<String, Integer> dateCases) {
	this.dateCases = dateCases;
    }

    @Override
    public String toString() {
	return "LocationStatsDTO{" +
	    "ID = '" + id + '\'' +
	    ", state='" + state + '\'' +
	    ", country='" + country + '\'' +
	    ", lastUpdatedValue=" + lastUpdatedValue +
	    ", newData=" + newData +
	    '}';
    }
}
