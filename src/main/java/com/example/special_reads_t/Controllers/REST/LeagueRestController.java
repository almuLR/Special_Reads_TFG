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

    /**
     * GET /api/leagues
     * Obtiene ligas por defecto e internas, y flag de límite.
     */
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

    /**
     * POST /api/leagues
     * Crea una nueva liga interna.
     */
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

    /**
     * GET /api/leagues/new
     * Obtiene amigos para seleccionar al crear liga.
     */
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

    /**
     * GET /api/leagues/{id}
     * Obtiene detalles de liga e información de miembros.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeagueDetailResponse> getLeagueDetail(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Obtiene DTO interno
        Optional<LeagueDto> optDto = leagueService.getInternalLeaguesDtos(currentUser).stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
        if (!optDto.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        LeagueDto dto = optDto.get();
        // Obtiene entidad para participantes
        Optional<League> leagueOpt = leagueService.getLeagueById(id);
        if (!leagueOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<User> participants = leagueOpt.get().getParticipants();
        // Construye lista de amigos marcando participantes
        List<FriendMembershipDto> members = userService.getFriendsOf(currentUser).stream()
                .map(u -> new FriendMembershipDto(
                        u.getId(), u.getUsername(), participants.contains(u)
                ))
                .collect(Collectors.toList());
        LeagueDetailResponse resp = new LeagueDetailResponse(dto, members);
        return ResponseEntity.ok(resp);
    }

    /**
     * PUT /api/leagues/{id}
     * Actualiza una liga existente.
     */
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

    // === DTOs internos ===
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
        // getters/setters omitidos
    }

    public static class SimpleUserDto {
        private Long id;
        private String username;
        public SimpleUserDto(Long id, String username) {
            this.id = id;
            this.username = username;
        }
        // getters/setters omitidos
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
        // getters/setters omitidos
    }

    public static class LeagueDetailResponse {
        private LeagueDto league;
        private List<FriendMembershipDto> friends;
        public LeagueDetailResponse(LeagueDto league, List<FriendMembershipDto> friends) {
            this.league = league;
            this.friends = friends;
        }
        // getters/setters omitidos
    }
}
