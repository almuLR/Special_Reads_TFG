package com.example.special_reads_t.Integracion;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserSignIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterUserIntegration() throws Exception {
        mockMvc.perform(post("/signUp")
                        .with(csrf())
                        .param("username", "TestUser")
                        .param("password", "testPassword")
                        .param("email", "TestUser@Example.com")
                        .param("dateOfBirth", "1980-01-01")
                        .param("favoriteGenres", "Fiction", "Adventure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login**"));

        User savedUser = userRepository.findByUsername("TestUser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo("testPassword");
        assertThat(savedUser.getDateOfBirth()).isEqualTo("1980-01-01");
        assertThat(savedUser.getRoles()).containsExactly("USER");
        assertThat(savedUser.getUsername()).isEqualTo("TestUser");
        assertThat(savedUser.getEmail()).isEqualTo("TestUser@Example.com");
        assertThat(savedUser.getFavoriteGenres()).containsExactly("Fiction", "Adventure");
    }
}
