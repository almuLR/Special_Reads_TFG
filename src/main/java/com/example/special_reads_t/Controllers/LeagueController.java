package com.example.special_reads_t.Controllers;

import com.example.special_reads_t.Model.League;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.LeagueDto;
import com.example.special_reads_t.Model.dto.RankingDto;
import com.example.special_reads_t.Repository.LeagueRepository;
import com.example.special_reads_t.Service.LeagueService;
import com.example.special_reads_t.Service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class LeagueController {
    @Autowired
    private LeagueService leagueService;

    @Autowired
    private UserService userService;

    @GetMapping("/ranking")
    public String showRankings(Model model) {
        User currentUser = userService.getCurrentUser();

        List<LeagueDto> defaultsLeagues = leagueService.getDefaultsLeaguesDtos(currentUser);

        List<LeagueDto> internalLeagues = leagueService.getInternalLeaguesDtos(currentUser);

        model.addAttribute("defaultsLeagues", defaultsLeagues);
        model.addAttribute("internalLeagues", internalLeagues);

        boolean maxedOut = (internalLeagues.size() >= 3);
        model.addAttribute("maxedOut", maxedOut);
        return "ranking";
    }

    @PostMapping("/newRanking")
    public String createNewRanking(@RequestParam("title") String title, @RequestParam("booksGoal") int booksGoal, @RequestParam(value = "participantIds") List<Long> participantIds, Model model) {
        User currentUser = userService.getCurrentUser();

        if (leagueService.getInternalLeaguesForUser(currentUser).size() >= 3) {
            return "error";
        }

        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("errorMessage", "La liga no puede ser innombrable. Ponle un nombre.");
            return "error";
        }

        if (booksGoal < 1) {
            model.addAttribute("errorMessage", "¿Libros a leer = 0? Pon al menos 1.");
            return "error";
        }

        // Si no se selecciona a nadie, pues la liga será el 'club solitario'
        if (participantIds == null || participantIds.isEmpty()) {
            model.addAttribute("errorMessage", "¡No puedes jugar esta liga tú solito! Selecciona al menos un amigo.");
            return "error";
        }

        List<User> participants = userService.getUsersByIds(participantIds);

        if (!participants.contains(currentUser)) {
            participants.add(currentUser);
        }

        leagueService.createInternalLeague(title, booksGoal, participants);

        return "redirect:/ranking";
    }

    @GetMapping("/newRanking")
    public String newRankingTemplate(Model model) {
        User currentUser = userService.getCurrentUser();
        List<User> friends = userService.getFriendsOf(currentUser);
        model.addAttribute("friends", friends);
        return "newRanking";
    }

    @GetMapping("/editRanking/{id}")
    public String editRankingTemplate(@PathVariable("id") Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        List<User> friends = userService.getFriendsOf(currentUser);

        Optional<League> leagueOptional = leagueService.getLeagueById(id);
        if (!leagueOptional.isPresent()) {
            model.addAttribute("errorMessage", "Liga no encontrada.");
        }
        League league = leagueOptional.get();

        List<Map<String, Object>> friendList = new ArrayList<>();
        for (User friend : friends) {
            Map<String, Object> friendMap = new HashMap<>();
            friendMap.put("id", friend.getId());
            friendMap.put("username", friend.getUsername());
            friendMap.put("inLeague", league.getParticipants().contains(friend));
            friendList.add(friendMap);
        }
        model.addAttribute("league", league);
        model.addAttribute("friends", friendList);
        return "editRanking";
    }

    @PostMapping("/editRanking/{id}")
    public String updateInternalLeague(@PathVariable("id") Long leagueId,
                                       @RequestParam("title") String title,
                                       @RequestParam("booksGoal") int booksGoal,
                                       @RequestParam("participantIds") List<Long> participantIds,
                                       Model model) {
        User currentUser = userService.getCurrentUser();

        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El título no puede estar vacío.");
            return "error";
        }
        if (booksGoal < 1) {
            model.addAttribute("errorMessage", "El objetivo de libros debe ser al menos 1.");
            return "error";
        }
        if (participantIds == null || participantIds.isEmpty()) {
            model.addAttribute("errorMessage", "Debes seleccionar al menos un amigo.");
            return "error";
        }

        List<User> participants = userService.getUsersByIds(participantIds);
        if (!participants.contains(currentUser)) {
            participants.add(currentUser);
        }

        League updatedLeague = leagueService.updateInternalLeague(leagueId, title, booksGoal, participants);
        if (updatedLeague == null) {
            model.addAttribute("errorMessage", "Error al actualizar la liga interna.");
            return "error";
        }
        return "redirect:/ranking";
    }
}

