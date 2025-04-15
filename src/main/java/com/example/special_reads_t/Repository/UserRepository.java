package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findByUsernameContainingIgnoreCase(String username);

    List<User> findByCountry(String country);
}
