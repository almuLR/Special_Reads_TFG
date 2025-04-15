package com.example.special_reads_t.Security;


import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DatabaseUsersLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void initDatabase() {
        List<User> existingUsers = userRepository.findAll();

        if (existingUsers.isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("pepitoperez@gmail.com");
            user.setDateOfBirth(LocalDate.of(1990, 1, 1));
            user.setPassword(passwordEncoder.encode("pass"));
            user.setDescription("Usuario de prueba");
            user.setRoles(Arrays.asList("USER"));
            user.setFavoriteGenres(Arrays.asList("Fantasia", "Aventura"));
            userRepository.save(user);

            // Usuario administrador
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("a@gmail.com");
            admin.setDateOfBirth(LocalDate.of(2003, 7, 4));
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setDescription("Administrador del sistema");
            admin.setRoles(Arrays.asList("USER", "ADMIN"));
            admin.setFavoriteGenres(Arrays.asList("CienciaFiccion", "Historico"));
            userRepository.save(admin);
        }
    }
}