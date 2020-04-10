package com.chrisb.coronavirustracker.data;

import java.util.ArrayList;
import java.util.List;

public class CovidWorldTimelineDTO {

    private String date;
    private List<WorldMapCountryDTO> list = new ArrayList<>();

    public CovidWorldTimelineDTO(){}

    public CovidWorldTimelineDTO(String date, List<WorldMapCountryDTO> list) {
	this.date = date;
	this.list = list;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public List<WorldMapCountryDTO> getList() {
	return list;
    }

    public void setList(List<WorldMapCountryDTO> list) {
	this.list = list;
    }
}
