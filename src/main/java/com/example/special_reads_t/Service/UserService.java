package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.UserDto;
import com.example.special_reads_t.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDto registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already in use");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setDescription(userDto.getDescription());
        user.setEncodedPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(List.of("USER"));
        if (userDto.getFavoriteGenres() != null) {
            user.setFavoriteGenres(userDto.getFavoriteGenres());
        }
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setDescription(user.getDescription());
        dto.setFavoriteGenres(user.getFavoriteGenres());
        return dto;
    }


}
