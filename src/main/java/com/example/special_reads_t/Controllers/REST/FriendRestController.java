package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.Friend;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.FriendJournalProgressDto;
import com.example.special_reads_t.Repository.FriendRepository;
import com.example.special_reads_t.Service.FriendService;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
public class FriendRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private FriendRepository friendRepository;

    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestParam("friendId") Long friendId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        User friendUser = userService.findById(friendId).orElseThrow(() ->
                new IllegalArgumentException("No se encontro amigo con ese Id: " + friendId));

        if (currentUser.getId().equals(friendId)) {
            return ResponseEntity.badRequest().body("No puedes enviarte una solicitud a ti mismo");
        }

        Optional<Friend> exisingRequest = friendService.getRequest(currentUser, friendUser);
        if (exisingRequest.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una solicitud de amistad pendiente o una amistad establecida");
        }

        Friend friendRequest = new Friend();
        friendRequest.setOwner(currentUser);
        friendRequest.setFriend(friendUser);
        friendRequest.setStatus("PENDIENTE");
        friendRepository.save(friendRequest);
        return ResponseEntity.ok("Solicitud de amistad enviada");
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendJournalProgressDto>> getReceivedFriendRequests() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Friend> pendingRequest = friendRepository.findByFriendAndStatus(currentUser, "PENDIENTE");

        List<FriendJournalProgressDto> dtoList = pendingRequest.stream()
                .map(friend -> {
                    FriendJournalProgressDto dto = new FriendJournalProgressDto();
                    dto.setId(friend.getId());
                    dto.setFriendName(friend.getOwner().getUsername());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptRequest(@RequestParam("friendId") Long friendId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado");
        }

        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("No existe la solicitud con id: " + friendId));

        if (!friendRequest.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes aceptar una solicitud ajena");
        }

        friendRequest.setStatus("ACEPTADA");
        friendRepository.save(friendRequest);
        return ResponseEntity.ok("Solicitud aceptada");
    }

    @PostMapping("/decline")
    public ResponseEntity<String> declineRequest(@RequestParam("friendId") Long friendId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado");
        }

        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("No existe la solicitud con id: " + friendId));

        if (!friendRequest.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes rechazar una solicitud ajena");
        }


        friendRequest.setStatus("RECHAZADA");
        friendRepository.save(friendRequest);
        return ResponseEntity.ok("Solicitud rechazada");
    }

    @GetMapping("/accept")
    public ResponseEntity<List<FriendJournalProgressDto>> getAcceptedFriends() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Friend> acceptedFriends = friendRepository.findAcceptedFriendsForUser(currentUser);

        List<FriendJournalProgressDto> dtoList = acceptedFriends.stream()
                .flatMap(friendship -> {
                    User otherUser = (friendship.getOwner().equals(currentUser))
                            ? friendship.getFriend()
                            : friendship.getOwner();

                    if (otherUser.getJournalEntries() != null) {
                        return otherUser.getJournalEntries().stream()
                                .filter(entry -> entry.getStatus() != null
                                        && entry.getStatus().equalsIgnoreCase("Leyendo"))
                                .map(entry -> {
                                    FriendJournalProgressDto dto = new FriendJournalProgressDto();
                                    dto.setId(friendship.getId());
                                    dto.setFriendName(otherUser.getUsername());
                                    dto.setBookTitle(entry.getBook().getTitle());
                                    dto.setProgress(entry.getProgress());
                                    dto.setProfilePhotoUrl(otherUser.getProfilePhotoUrl());
                                    return dto;
                                });
                    }
                    return java.util.stream.Stream.empty();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }


}
