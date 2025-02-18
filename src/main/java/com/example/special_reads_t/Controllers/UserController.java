package com.example.special_reads_t.Controllers;


import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.UserDto;
import com.example.special_reads_t.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/signUp")
    public String signUpTemplate(Model model) {
        model.addAttribute("user", new User());
        return "signUp";
    }

    @PostMapping("/submitForm")
    public String registerUser(User user) {
        System.out.println("Entrando en registerUser");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList("USER"));
        //user.setFavoriteGenres(user.getFavoriteGenres());
        userService.save(user);
        return "redirect:/login";
    }
}
