package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.Challenge;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.ChallengeService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para desafíos (challenges).
 */
@RestController
@RequestMapping("/api/challenges")
public class ChallengesRestController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private UserService userService;

    /**
     * Obtiene la lista de desafíos para el usuario autenticado.
     * @return 200 OK con la lista de Challenge, o 401 si no está autenticado.
     */
    @GetMapping
    public ResponseEntity<List<Challenge>> getChallenges() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Challenge> challenges = challengeService.getChallengesFor(currentUser);
        return ResponseEntity.ok(challenges);
    }
}
