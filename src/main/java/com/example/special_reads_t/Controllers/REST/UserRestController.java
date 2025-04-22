package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Security.FileStorageConfig;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador REST para registro y perfil de usuario.
 */
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageConfig fileStorageConfig;

    /**
     * GET /api/users/current
     * Devuelve datos básicos del usuario autenticado.
     */
    @GetMapping("/current")
    public ResponseEntity<UserSummaryResponse> getCurrentUserSummary() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserSummaryResponse summary = new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                Optional.ofNullable(user.getProfilePhotoUrl()).orElse("/images/iconoPerfil.png")
        );
        return ResponseEntity.ok(summary);
    }

    /**
     * POST /api/users/signup
     * Registra un nuevo usuario.
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> registerUser(@RequestBody SignupRequest req) {
        // Crea nuevo usuario sin validación explícita de unicidad (el servicio debe manejarla si es necesario)
        User user = new User();
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setCountry(req.country);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setRoles(Collections.singletonList("USER"));
        user.setDateOfBirth(LocalDate.parse(req.dateOfBirth, DateTimeFormatter.ISO_DATE));
        user.setDescription(req.description);
        user.setFavoriteGenres(req.favoriteGenres);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /api/users/me
     * Obtiene datos de perfil del usuario autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ProfileResponse resp = new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getDateOfBirth().toString(),
                user.getDescription(),
                user.getFavoriteGenres(),
                Optional.ofNullable(user.getProfilePhotoUrl()).orElse("/images/iconoPerfil.png")
        );
        return ResponseEntity.ok(resp);
    }

    /**
     * PUT /api/users/me
     * Actualiza datos de perfil y foto (multipart/form-data).
     */
    @PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfile(
            @RequestPart("data") ProfileUpdateRequest req,
            @RequestPart(value = "profilePicture", required = false) MultipartFile file
    ) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setDescription(req.description);
        user.setDateOfBirth(LocalDate.parse(req.dateOfBirth, DateTimeFormatter.ISO_DATE));
        user.setFavoriteGenres(req.favoriteGenres);
        if (file != null && !file.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
                Path uploadDir = Paths.get(fileStorageConfig.getUploadDir());
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
                Path filePath = uploadDir.resolve(filename);
                file.transferTo(filePath.toFile());
                user.setProfilePhotoUrl("/images/uploads/" + filename);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    // === DTOs internos ===
    public static class SignupRequest {
        public String username;
        public String password;
        public String email;
        public String country;
        public String dateOfBirth; // ISO: yyyy-MM-dd
        public String description;
        public List<String> favoriteGenres;
    }

    public static class ProfileResponse {
        public String username;
        public String email;
        public String dateOfBirth;
        public String description;
        public List<String> favoriteGenres;
        public String profilePhotoUrl;

        public ProfileResponse(String username, String email, String dateOfBirth,
                               String description, List<String> favoriteGenres, String profilePhotoUrl) {
            this.username = username;
            this.email = email;
            this.dateOfBirth = dateOfBirth;
            this.description = description;
            this.favoriteGenres = favoriteGenres;
            this.profilePhotoUrl = profilePhotoUrl;
        }
    }

    public static class ProfileUpdateRequest {
        public String username;
        public String email;
        public String dateOfBirth;
        public String description;
        public List<String> favoriteGenres;
    }

    public static class UserSummaryResponse {
        public Long id;
        public String username;
        public String email;
        public String profilePhotoUrl;

        public UserSummaryResponse(Long id, String username, String email, String profilePhotoUrl) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.profilePhotoUrl = profilePhotoUrl;
        }
    }
    @GetMapping
    public ResponseEntity<List<UserBasicDto>> searchUsers(@RequestParam("username") String username) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<User> users = userService.findUsersByUsernameContaining(username);

        List<UserBasicDto> filtered = users.stream()
                .filter(u -> !u.getId().equals(currentUser.getId())) // Excluirse a sí mismo
                .map(u -> new UserBasicDto(u.getId(), u.getUsername()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtered);
    }

    public static class UserBasicDto {
        public Long id;
        public String username;

        public UserBasicDto(Long id, String username) {
            this.id = id;
            this.username = username;
        }
    }
}
