package com.chrisb.coronavirustracker.data;

public class WorldMapCountryDTO {

    private String id;

    private int confirmed;

    private int deaths;

    private int recovered;

    public WorldMapCountryDTO(){}

    public WorldMapCountryDTO(String id, int confirmed, int deaths, int recovered) {
	this.id = id;
	this.confirmed = confirmed;
	this.deaths = deaths;
	this.recovered = recovered;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public int getConfirmed() {
	return confirmed;
    }

    public void setConfirmed(int confirmed) {
	this.confirmed = confirmed;
    }

    public int getDeaths() {
	return deaths;
    }

    public void setDeaths(int deaths) {
	this.deaths = deaths;
    }

    public int getRecovered() {
	return recovered;
    }

    public void setRecovered(int recovered) {
	this.recovered = recovered;
    }
}
