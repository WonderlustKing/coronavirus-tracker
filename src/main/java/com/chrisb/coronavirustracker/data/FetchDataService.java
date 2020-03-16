package com.chrisb.coronavirustracker.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FetchDataService {

    private static final String CONFIRMED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static final String RECOVERS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";
    private static final String DEATHS_URL_DATA = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";

    private List<LocationStatsDTO> confirmedData = new ArrayList<>();
    public List<LocationStatsDTO> getConfirmedData() {
	return confirmedData;
    }

    private List<LocationStatsDTO> deathsData = new ArrayList<>();
    public List<LocationStatsDTO> getDeathsData() {
	return deathsData;
    }

    private List<LocationStatsDTO> recoversData = new ArrayList<>();
    public List<LocationStatsDTO> getRecoversData() {
	return recoversData;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */4 * * *")
    public void fetchConfirmedCases() throws IOException, InterruptedException {
        HttpResponse<String> dataResponse = getCSVDataResponseFrom(CONFIRMED_DATA_URL);
	System.out.println("fetched confirmed cases data at: " + LocalDateTime.now());

	Reader reader = new StringReader(dataResponse.body());
	Iterable<CSVRecord> csvRecordsResponse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
	this.confirmedData = convertFromCSVtoList(csvRecordsResponse)
	    .stream()
	    .sorted(Comparator.comparingInt(LocationStatsDTO::getLastUpdatedValue).reversed())
	    .collect(Collectors.toList());
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */4 * * *")
    public void fetchDeathCases() throws IOException, InterruptedException {
        HttpResponse<String> dataResponse = getCSVDataResponseFrom(DEATHS_URL_DATA);
	System.out.println("fetched deaths cases data at: " + LocalDateTime.now());

	Reader reader = new StringReader(dataResponse.body());
	Iterable<CSVRecord> csvRecordsResponse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
	this.deathsData = convertFromCSVtoList(csvRecordsResponse)
	    .stream()
	    .sorted(Comparator.comparingInt(LocationStatsDTO::getLastUpdatedValue).reversed())
	    .collect(Collectors.toList());
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */4 * * *")
    public void fetchRecoveredCases() throws IOException, InterruptedException {
        HttpResponse<String> dataResponse = getCSVDataResponseFrom(RECOVERS_DATA_URL);
	System.out.println("fetched recovered cases data at: " + LocalDateTime.now());

	Reader reader = new StringReader(dataResponse.body());
	Iterable<CSVRecord> csvRecordsResponse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
	this.recoversData = convertFromCSVtoList(csvRecordsResponse)
	    .stream()
	    .sorted(Comparator.comparingInt(LocationStatsDTO::getLastUpdatedValue).reversed())
	    .collect(Collectors.toList());
    }

    private HttpResponse<String> getCSVDataResponseFrom(String url) throws IOException, InterruptedException {
	HttpClient httpClient = HttpClient.newHttpClient();

	HttpRequest dataRequest = HttpRequest.newBuilder()
	    .GET()
	    .uri(URI.create(url))
	    .build();

	return httpClient.send(dataRequest, HttpResponse.BodyHandlers.ofString());
    }

    private List<LocationStatsDTO> convertFromCSVtoList(Iterable<CSVRecord> records) {
	List<LocationStatsDTO> newConfirmedData = new ArrayList<>();

	for (CSVRecord record : records) {
	    String state = record.get("Province/State");
	    String country = record.get("Country/Region");
	    int lastUpdatedValue = Integer.parseInt(record.get(record.size() - 1));
	    int newUpdatedValue = lastUpdatedValue - Integer.parseInt(record.get(record.size() - 2));
	    if (hasLastUpdatedValue(lastUpdatedValue)) {
		LocationStatsDTO locationDTO = new LocationStatsDTO(state, country, lastUpdatedValue, newUpdatedValue);
		newConfirmedData.add(locationDTO);
	    }
	}
	return newConfirmedData;
    }

    private boolean hasLastUpdatedValue(int value) {
        return value > 0;
    }
}
