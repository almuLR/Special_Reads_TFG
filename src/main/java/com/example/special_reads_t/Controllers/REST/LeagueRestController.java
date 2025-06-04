package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.League;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.LeagueDto;
import com.example.special_reads_t.Service.LeagueService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leagues")
public class LeagueRestController {

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<LeagueRankingResponse> getRankings() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<LeagueDto> defaultsLeagues = leagueService.getDefaultsLeaguesDtos(currentUser);
        List<LeagueDto> internalLeagues = leagueService.getInternalLeaguesDtos(currentUser);
        boolean maxedOut = internalLeagues.size() >= 3;
        LeagueRankingResponse resp = new LeagueRankingResponse(defaultsLeagues, internalLeagues, maxedOut);
        return ResponseEntity.ok(resp);
    }


    @PostMapping
    public ResponseEntity<Void> createLeague(
            @RequestParam String title,
            @RequestParam int booksGoal,
            @RequestParam List<Long> participantIds) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (leagueService.getInternalLeaguesForUser(currentUser).size() >= 3) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (title == null || title.trim().isEmpty() || booksGoal < 1 || participantIds == null || participantIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<User> participants = userService.getUsersByIds(participantIds);
        if (!participants.contains(currentUser)) participants.add(currentUser);
        leagueService.createInternalLeague(title, booksGoal, participants);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/new")
    public ResponseEntity<List<SimpleUserDto>> getNewLeagueUsers() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<SimpleUserDto> list = userService.getFriendsOf(currentUser).stream()
                .map(u -> new SimpleUserDto(u.getId(), u.getUsername()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{id}")
    public ResponseEntity<LeagueDetailResponse> getLeagueDetail(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<LeagueDto> optDto = leagueService.getInternalLeaguesDtos(currentUser).stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
        if (!optDto.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        LeagueDto dto = optDto.get();
        Optional<League> leagueOpt = leagueService.getLeagueById(id);
        if (!leagueOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<User> participants = leagueOpt.get().getParticipants();
        List<FriendMembershipDto> members = userService.getFriendsOf(currentUser).stream()
                .map(u -> new FriendMembershipDto(
                        u.getId(), u.getUsername(), participants.contains(u)
                ))
                .collect(Collectors.toList());
        LeagueDetailResponse resp = new LeagueDetailResponse(dto, members);
        return ResponseEntity.ok(resp);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLeague(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam int booksGoal,
            @RequestParam List<Long> participantIds) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (title == null || title.trim().isEmpty() || booksGoal < 1
                || participantIds == null || participantIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<User> participants = userService.getUsersByIds(participantIds);
        if (!participants.contains(currentUser)) participants.add(currentUser);
        League updated = leagueService.updateInternalLeague(id, title, booksGoal, participants);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    public static class LeagueRankingResponse {
        private List<LeagueDto> defaultsLeagues;
        private List<LeagueDto> internalLeagues;
        private boolean maxedOut;
        public LeagueRankingResponse(List<LeagueDto> defaultsLeagues,
                                     List<LeagueDto> internalLeagues,
                                     boolean maxedOut) {
            this.defaultsLeagues = defaultsLeagues;
            this.internalLeagues = internalLeagues;
            this.maxedOut = maxedOut;
        }
        public List<LeagueDto> getDefaultsLeagues() {
            return defaultsLeagues;
        }

        public void setDefaultsLeagues(List<LeagueDto> defaultsLeagues) {
            this.defaultsLeagues = defaultsLeagues;
        }

        public List<LeagueDto> getInternalLeagues() {
            return internalLeagues;
        }

        public void setInternalLeagues(List<LeagueDto> internalLeagues) {
            this.internalLeagues = internalLeagues;
        }

        public boolean isMaxedOut() {
            return maxedOut;
        }

        public void setMaxedOut(boolean maxedOut) {
            this.maxedOut = maxedOut;
        }

    }

    public static class SimpleUserDto {
        private Long id;
        private String username;
        public SimpleUserDto(Long id, String username) {
            this.id = id;
            this.username = username;
        }
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }

    public static class FriendMembershipDto {
        private Long id;
        private String username;
        private boolean inLeague;
        public FriendMembershipDto(Long id, String username, boolean inLeague) {
            this.id = id;
            this.username = username;
            this.inLeague = inLeague;
        }
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isInLeague() {
            return inLeague;
        }

        public void setInLeague(boolean inLeague) {
            this.inLeague = inLeague;
        }

    }

    public static class LeagueDetailResponse {
        private LeagueDto league;
        private List<FriendMembershipDto> friends;
        public LeagueDetailResponse(LeagueDto league, List<FriendMembershipDto> friends) {
            this.league = league;
            this.friends = friends;
        }
        public LeagueDto getLeague() {
            return league;
        }

        public void setLeague(LeagueDto league) {
            this.league = league;
        }

        public List<FriendMembershipDto> getFriends() {
            return friends;
        }

        public void setFriends(List<FriendMembershipDto> friends) {
            this.friends = friends;
        }

    }
}
