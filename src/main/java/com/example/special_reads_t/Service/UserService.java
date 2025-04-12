package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.FriendRepository;
import com.example.special_reads_t.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FriendRepository friendRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {return userRepository.findById(id);}

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }

    public List<User> findUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public List<User> findUsersAvaibleForFriendShip(String username, User currentUser) {
        List<User> allUsers = userRepository.findByUsernameContainingIgnoreCase(username);

        Set<Long> excludedIds = friendRepository.findByOwner(currentUser)
                .stream()
                .filter(friend -> "PENDIENTE".equalsIgnoreCase(friend.getStatus())
                                            || "ACEPTADA".equalsIgnoreCase(friend.getStatus()))
                .map(friend -> friend.getFriend().getId())
                .collect(Collectors.toSet());
        return allUsers.stream().filter(user -> !excludedIds.contains(user.getId())).collect(Collectors.toList());
    }

}
