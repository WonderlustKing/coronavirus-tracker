package com.chrisb.coronavirustracker.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HomeController {

    private FetchDataService fetchDataService;

    @Autowired
    public HomeController(FetchDataService fetchDataService) {
	this.fetchDataService = fetchDataService;
    }

    @GetMapping("/")
    public String home(Model model) {
	List<LocationStatsDTO> confirmed = fetchDataService.getConfirmedData();
        model.addAttribute("confirmedStats", confirmed);
        model.addAttribute("confirmedTotalCases", confirmed.stream()
	    .mapToInt(LocationStatsDTO::getLastUpdatedValue)
	    .sum());
        model.addAttribute("newTotalConfirmedCases", confirmed.stream()
	    .mapToInt(LocationStatsDTO::getNewData)
	    .sum());

        List<LocationStatsDTO> deaths = fetchDataService.getDeathsData();
        model.addAttribute("deathsStats", deaths);
        model.addAttribute("totalDeaths", deaths.stream()
	    .mapToInt(LocationStatsDTO::getLastUpdatedValue)
	    .sum());
        model.addAttribute("newDeaths", deaths.stream()
	    .mapToInt(LocationStatsDTO::getNewData)
	    .sum());

        List<LocationStatsDTO> recovers = fetchDataService.getRecoversData();
	model.addAttribute("recoversStats", recovers);
	model.addAttribute("totalRecovers", recovers.stream()
	    .mapToInt(LocationStatsDTO::getLastUpdatedValue)
	    .sum());
	model.addAttribute("newRecovers", recovers.stream()
	    .mapToInt(LocationStatsDTO::getNewData)
	    .sum());

	LocalDate yesterday = LocalDate.now().minusDays(1);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	model.addAttribute("yesterday", yesterday.format(formatter));

	List<CovidWorldTimelineDTO> covidWorldTimeLine = fetchDataService.getWorldTimeline();
	List<WorldMapCountryDTO> todayWorldTimeLine = covidWorldTimeLine.get(covidWorldTimeLine.size() - 1).getList();

	model.addAttribute("worldTimeline", covidWorldTimeLine);
	model.addAttribute("todayWorldTimeline", todayWorldTimeLine);

        return "home";
    }
}
