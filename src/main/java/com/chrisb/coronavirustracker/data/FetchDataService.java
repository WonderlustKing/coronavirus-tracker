package com.chrisb.coronavirustracker.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class FetchDataService {

    private static final String CONFIRMED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final String RECOVERS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private static final String DEATHS_URL_DATA = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static final String CORONA_VIRUS_START_DATE = "1/22/20";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yy");


    private static final Map<String, String> COUNTRIES_ISO_CODES;
    static {

	List<Map<String, String>> countriesWithIso = new ArrayList<>();
	try {
	    InputStream countriesIso = new ClassPathResource("data/country_iso_codes.json").getInputStream();
	    countriesWithIso = new ObjectMapper().readValue(countriesIso, new TypeReference<>() {});
	} catch (IOException e) {
	    e.printStackTrace();
	}

	COUNTRIES_ISO_CODES = countriesWithIso.stream().
	    collect(Collectors.toMap(s -> (String) s.get("Code"), s -> (String) s.get("Name")));
    }

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

    private List<CovidWorldTimelineDTO> worldTimeline = new ArrayList<>();
    public List<CovidWorldTimelineDTO> getWorldTimeline() {
        if (worldTimeline.isEmpty()) {
            initWorldTimelineData();
	}
	return worldTimeline;
    }

    private void initWorldTimelineData() {
	List<CovidWorldTimelineDTO> dateCases = new ArrayList<>();
	LocalDate start = LocalDate.parse(CORONA_VIRUS_START_DATE, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

	for (LocalDate date = start; date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
	    int chinaConfirmedCases = 0, chinaDeaths = 0, chinaRecovers = 0;
	    int canadaConfirmedCases = 0, canadaDeaths = 0, canadaRecovers = 0;
	    int australiaConfirmedCases = 0, australiaDeaths = 0, australiaRecovers = 0;

	    List<WorldMapCountryDTO> data = new ArrayList<>();
	    for (LocationStatsDTO confirmed : confirmedData) {
	        WorldMapCountryDTO worldMapCountryDTO = new WorldMapCountryDTO();
	        worldMapCountryDTO.setId(confirmed.getId());
	        int confirmsNumber = confirmed.getDateCases().get(FORMATTER.format(date));
		worldMapCountryDTO.setConfirmed(confirmsNumber);

		String countryId = confirmed.getId().equals("") ? confirmed.getCountry() : confirmed.getId();
		int deathsNumber = getDeathsNumberOf(date, countryId, confirmed.getState());
		worldMapCountryDTO.setDeaths(deathsNumber);

		int recoversNumber = getRecoversNumberOf(date, countryId, confirmed.getState());
		worldMapCountryDTO.setRecovered(recoversNumber);

		if ("China".equals(confirmed.getCountry())) {
		    chinaConfirmedCases += confirmsNumber;
		    chinaDeaths += deathsNumber;
		    chinaRecovers += recoversNumber;
		    worldMapCountryDTO.setId("CN");
		    worldMapCountryDTO.setConfirmed(chinaConfirmedCases);
		    worldMapCountryDTO.setDeaths(chinaDeaths);
		    worldMapCountryDTO.setRecovered(chinaRecovers);
		} else if ("Canada".equals(confirmed.getCountry())) {
		    canadaConfirmedCases +=confirmsNumber;
		    canadaDeaths += deathsNumber;
		    canadaRecovers += recoversNumber;
		    worldMapCountryDTO.setConfirmed(canadaConfirmedCases);
		    worldMapCountryDTO.setDeaths(canadaDeaths);
		    worldMapCountryDTO.setRecovered(canadaRecovers);
		    worldMapCountryDTO.setId("CA");
		} else if ("Australia".equals(confirmed.getCountry())) {
		    australiaConfirmedCases += confirmsNumber;
		    australiaDeaths += deathsNumber;
		    australiaRecovers += recoversNumber;
		    worldMapCountryDTO.setId("AU");
		    worldMapCountryDTO.setConfirmed(australiaConfirmedCases);
		    worldMapCountryDTO.setDeaths(australiaDeaths);
		    worldMapCountryDTO.setRecovered(australiaRecovers);
		}

		data.add(worldMapCountryDTO);
	    }
	    CovidWorldTimelineDTO covidWorldTimelineDTO = new CovidWorldTimelineDTO(FORMATTER.format(date), data);
	    dateCases.add(covidWorldTimelineDTO);
	}
	worldTimeline = dateCases;
    }

    private int getDeathsNumberOf(LocalDate date, String countryId, String state) {
        Optional<LocationStatsDTO> optionalDeaths = getDeathsData()
	    .stream()
	    .filter(d -> countryIdFilter(d, state).test(countryId))
	    .findFirst();

	int deathsNumber = 0;
	if (optionalDeaths.isPresent()) {
	    deathsNumber = optionalDeaths.get().getDateCases().get(FORMATTER.format(date));
	}
	return deathsNumber;
    }

    private int getRecoversNumberOf(LocalDate date, String countryId, String state) {
	Optional<LocationStatsDTO> optionalRecovers = getRecoversData()
	    .stream()
	    .filter(d -> countryIdFilter(d, state).test(countryId))
	    .findFirst();

	int recoversNumber = 0;
	if (optionalRecovers.isPresent()) {
	    recoversNumber = optionalRecovers.get().getDateCases().get(FORMATTER.format(date));
	}
	return recoversNumber;
    }

    private Predicate<String> countryIdFilter(LocationStatsDTO locationDTO, String state) {
	return
	    countryId -> {
		if (("China".equals(countryId) || "Canada".equals(countryId) || "Australia".equals(countryId))
		    && locationDTO.getState().equals(state)) {
		    return true;
		} else {
		    return locationDTO.getId().equals(countryId);
		}
	    };
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */4 * * *") //scheduled to run every 4 hours
    public void fetchConfirmedCases() throws IOException, InterruptedException {
        HttpResponse<String> dataResponse = getCSVDataResponseFrom(CONFIRMED_DATA_URL);
	System.out.println("fetched confirmed cases data at: " + LocalDateTime.now());

	Reader reader = new StringReader(dataResponse.body());
	Iterable<CSVRecord> csvRecordsResponse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
	this.confirmedData = convertFromCSVtoList(csvRecordsResponse)
	    .stream()
	    .sorted(Comparator.comparingInt(LocationStatsDTO::getLastUpdatedValue).reversed())
	    .collect(Collectors.toList());

	if (!worldTimeline.isEmpty()) {
	    worldTimeline.clear();
	}
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */4 * * *") //scheduled to run every 4 hours
    public void fetchDeathCases() throws IOException, InterruptedException {
        HttpResponse<String> dataResponse = getCSVDataResponseFrom(DEATHS_URL_DATA);
	System.out.println("fetched deaths cases data at: " + LocalDateTime.now());

	Reader reader = new StringReader(dataResponse.body());
	Iterable<CSVRecord> csvRecordsResponse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
	this.deathsData = convertFromCSVtoList(csvRecordsResponse)
	    .stream()
	    .sorted(Comparator.comparingInt(LocationStatsDTO::getLastUpdatedValue).reversed())
	    .collect(Collectors.toList());

	if (!worldTimeline.isEmpty()) {
	    worldTimeline.clear();
	}    }

    @PostConstruct
    @Scheduled(cron = "0 0 */4 * * *") //scheduled to run every 4 hours
    public void fetchRecoveredCases() throws IOException, InterruptedException {
        HttpResponse<String> dataResponse = getCSVDataResponseFrom(RECOVERS_DATA_URL);
	System.out.println("fetched recovered cases data at: " + LocalDateTime.now());

	Reader reader = new StringReader(dataResponse.body());
	Iterable<CSVRecord> csvRecordsResponse = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
	this.recoversData = convertFromCSVtoList(csvRecordsResponse)
	    .stream()
	    .sorted(Comparator.comparingInt(LocationStatsDTO::getLastUpdatedValue).reversed())
	    .collect(Collectors.toList());

	if (!worldTimeline.isEmpty()) {
	    worldTimeline.clear();
	}
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
	    String id = (state != null && !state.equals("") ? getIdFrom(state) : getIdFrom(country));
	    int lastUpdatedValue = Integer.parseInt(record.get(record.size() - 1));
	    int newUpdatedValue = lastUpdatedValue - Integer.parseInt(record.get(record.size() - 2));
	    Map<String, Integer> dateCasesMap = getDateCasesUntilToday(record);
	    if (hasLastUpdatedValue(lastUpdatedValue)) {
		LocationStatsDTO locationDTO = new LocationStatsDTO(id, state, country, lastUpdatedValue, newUpdatedValue, dateCasesMap);
		newConfirmedData.add(locationDTO);
	    }
	}
	return newConfirmedData;
    }

    private Map<String, Integer> getDateCasesUntilToday(CSVRecord record) {
        Map<String, Integer> dateCases = new LinkedHashMap<>();
	LocalDate start = LocalDate.parse(CORONA_VIRUS_START_DATE, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");

	for (LocalDate date = start; date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
	    String formattedDate = date.format(formatter);
	    Integer casesOf = Integer.parseInt(record.get(formattedDate));
	    dateCases.put(date.format(formatter), casesOf);
	}
	return dateCases;
    }

    private String getIdFrom(String identifier) {
	return COUNTRIES_ISO_CODES.entrySet()
	    .stream()
	    .filter(entry -> Objects.equals(entry.getValue(), identifier))
	    .map(Map.Entry::getKey)
	    .findFirst()
	    .orElse("");

    }

    private boolean hasLastUpdatedValue(int value) {
        return value > 0;
    }
}
