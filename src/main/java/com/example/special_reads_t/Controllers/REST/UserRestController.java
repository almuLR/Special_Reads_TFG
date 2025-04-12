package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.UserDto;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List<UserDto> searchUsers(@RequestParam("username") String username) {
        User currentUser = userService.getCurrentUser();

        List<User> users = userService.findUsersAvaibleForFriendShip(username, currentUser);

        return users.stream().map(user ->{
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setPassword(null);
            dto.setDateOfBirth(Date.valueOf(user.getDateOfBirth()));
            dto.setDescription(user.getDescription());
            dto.setFavoriteGenres(user.getFavoriteGenres());
            dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
            return dto;
        }).collect(Collectors.toList());
    }
}