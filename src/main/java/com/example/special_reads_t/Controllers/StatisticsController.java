package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.StatisticsService;
import com.example.special_reads_t.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class StatisticsController {
    @Autowired
    private StatisticsService stats;
    @Autowired
    private UserService users;

    @GetMapping("/charts")
    public String charts(Model m) throws JsonProcessingException {
        User me = users.getCurrentUser();

        Map<String,Long> topAuthors = stats.topAuthors(me);
        Map<String,Long> topGenres  = stats.topGenres(me);
        Map<String,Long> monthly    = stats.monthlyFinishes(me);

        ObjectMapper mapper = new ObjectMapper();

        List<Map<String,Object>> authorsList = topAuthors.entrySet().stream()
                .map(e -> {
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",  e.getKey());
                    map.put("value", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());


        List<Map<String,Object>> genresList = topGenres.entrySet().stream()
                .map(e -> {
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",  e.getKey());
                    map.put("value", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        List<Map<String,Object>> monthlyList = monthly.entrySet().stream()
                .map(e -> {
                    Map<String,Object> map = new HashMap<>();
                    map.put("month", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());


        m.addAttribute("authorsJson", mapper.writeValueAsString(authorsList));
        m.addAttribute("genresJson",  mapper.writeValueAsString(genresList));
        m.addAttribute("monthlyJson", mapper.writeValueAsString(monthlyList));


        m.addAttribute("authors", stats.topAuthors(me));
        m.addAttribute("genres",  stats.topGenres(me));
        m.addAttribute("monthly", stats.monthlyFinishes(me));
        m.addAttribute("totBooks",     stats.totalBooks(me));
        m.addAttribute("totPending",   stats.pendingBooks(me));
        m.addAttribute("totPages",     stats.totalPages(me));
        m.addAttribute("totReading",  stats.totalReading(me));
        m.addAttribute("totChallenges",stats.completedChallenges(me));
        m.addAttribute("totFriends",   stats.totalFriends(me));
        return "charts";
    }
}
