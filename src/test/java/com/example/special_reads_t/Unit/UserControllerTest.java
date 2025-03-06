package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Controllers.UserController;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("testuser@example.com");
        user.setFavoriteGenres(Arrays.asList("Fiction", "Adventure"));

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("testPassword")).thenReturn(encodedPassword);

        String result = userController.registerUser(user);

        assertEquals(encodedPassword, user.getPassword());
        assertEquals(Collections.singletonList("USER"), user.getRoles());
        assertEquals("testUser", user.getUsername());
        assertEquals("testuser@example.com", user.getEmail());
        assertEquals(Arrays.asList("Fiction", "Adventure"), user.getFavoriteGenres());
        verify(userService).save(user);
        assertEquals("redirect:/login", result);
    }
}
