package com.example.special_reads_t.Controllers;


import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Security.FileStorageConfig;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @GetMapping("/signUp")
    public String signUpTemplate(Model model) {
        model.addAttribute("user", new User());
        return "signUp";
    }

    @PostMapping("/signUp")
    public String registerUser(User user) {
        System.out.println("Entrando en registerUser");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList("USER"));
        //user.setFavoriteGenres(user.getFavoriteGenres());
        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/profileEdit")
    public String profileEditTemplate(Model model) {
        User currentUser = userService.getCurrentUser();
        String currentUsername = currentUser.getUsername();
        String currentEmail = currentUser.getEmail();
        LocalDate currentDateBirth = currentUser.getDateOfBirth();
        String currentDescription = currentUser.getDescription();
        List<String> currentFavoritesGenres = currentUser.getFavoriteGenres() == null ? Collections.emptyList() : currentUser.getFavoriteGenres();

        model.addAttribute("currentUsername", currentUsername);

        model.addAttribute("currentEmail", currentEmail);

        model.addAttribute("currentDateBirth", currentDateBirth);
        if (currentDescription == null) {
            currentDescription = "";
        }
        model.addAttribute("currentDescription", currentDescription);

        Map<String, String> genreNameMap = Map.of(
                "Romance", "Romance",
                "Fantasia", "Fantasía",
                "CienciaFiccion", "Ciencia Ficción",
                "Psicologia", "Psicología",
                "Historia", "Historia",
                "Terror", "Terror",
                "Manga", "Manga"
        );

        List<Map<String, Object>> genresForTemplate = new ArrayList<>();
        for (Map.Entry<String, String> entry : genreNameMap.entrySet()) {
            Map<String, Object> genreMap = new HashMap<>();
            genreMap.put("value", entry.getKey());
            genreMap.put("label", entry.getValue());
            genreMap.put("selected", currentFavoritesGenres.contains(entry.getKey()));
            genresForTemplate.add(genreMap);
        }

        model.addAttribute("genres", genresForTemplate);
        model.addAttribute("profilePhotoUrl", currentUser.getProfilePhotoUrl() != null
                ? currentUser.getProfilePhotoUrl()
                : "/images/iconoPerfil.png");

        return "profileEdit";
    }

    @GetMapping("/profile")
    public String profileTemplate(Model model) {

        User currentUser = userService.getCurrentUser();
        String currentUsername = currentUser.getUsername();
        String currentEmail = currentUser.getEmail();
        String currentDescription = currentUser.getDescription();

        model.addAttribute("currentUsername", currentUsername);

        model.addAttribute("currentEmail", currentEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentUser.getDateOfBirth().format(formatter);
        model.addAttribute("currentDateBirth", formattedDate);        if (currentDescription == null) {
            currentDescription = "";
        }
        model.addAttribute("currentDescription", currentDescription);

        List<String> currentUserFavoritesGenres = currentUser.getFavoriteGenres() == null ? Collections.emptyList() : currentUser.getFavoriteGenres();

        List<String> favoritesGenres = new ArrayList<>();
        for (String genre : currentUserFavoritesGenres) {
            switch (genre){
                case "Romance":
                    favoritesGenres.add("Romance");
                    break;
                case "Fantasia":
                    favoritesGenres.add("Fantasía");
                    break;
                case "CienciaFiccion":
                    favoritesGenres.add("Ciencia Ficción");
                    break;
                case "Psicologia":
                    favoritesGenres.add("Psicología");
                    break;
                case "Historia":
                    favoritesGenres.add("Historia");
                    break;
                case "Terror":
                    favoritesGenres.add("Terror");
                    break;
                case "Manga":
                    favoritesGenres.add("Manga");
                    break;
            }
        }
        model.addAttribute("genres", favoritesGenres);

        String profilePhotoUrl = currentUser.getProfilePhotoUrl() != null
                ? currentUser.getProfilePhotoUrl()
                : "/images/iconoPerfil.png";
        System.out.println("URL de foto de perfil enviada a la vista: " + profilePhotoUrl);
        model.addAttribute("profilePhotoUrl", profilePhotoUrl);
        return "profile";
    }

    @PostMapping("/saveProfile")
    public String saveProfile(String username, String email, String birthdate, String description, String[] favoriteGenres, MultipartFile profilePicture){
        User currentUser = userService.getCurrentUser();

        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setDescription(description);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        currentUser.setDateOfBirth(LocalDate.parse(birthdate, formatter));

        List<String> allowedGenres = List.of("Romance", "Fantasia", "CienciaFiccion", "Psicologia", "Historia", "Terror", "Manga");

        List<String> trueFavoriteGenres = new ArrayList<>();
        if (favoriteGenres != null) {
            for (String genre : favoriteGenres) {
                if (allowedGenres.contains(genre)) {
                    trueFavoriteGenres.add(genre);
                }
            }
        }

        currentUser.setFavoriteGenres(trueFavoriteGenres);

        if (!profilePicture.isEmpty()) {
            try {
                String originalFilename = profilePicture.getOriginalFilename();
                // Sanitiza el nombre para evitar espacios
                String sanitizedFilename = originalFilename.replaceAll("\\s+", "_");
                String filename = UUID.randomUUID() + "_" + sanitizedFilename;

                // Usa la ruta absoluta definida en application.properties
                String uploadDir = fileStorageConfig.getUploadDir();
                Path path = Paths.get(uploadDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                Path filePath = path.resolve(filename);
                System.out.println("Guardando imagen en: " + filePath.toAbsolutePath());
                profilePicture.transferTo(filePath.toFile());

                currentUser.setProfilePhotoUrl("/images/uploads/" + filename);

            } catch (IOException e) {
                System.out.println("Error al guardar la imagen: " + e.getMessage());
                e.printStackTrace();
            }
        }
        userService.save(currentUser);
        return "redirect:/profile";
    }
}
