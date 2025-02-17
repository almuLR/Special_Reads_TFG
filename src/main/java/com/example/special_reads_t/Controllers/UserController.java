package com.example.special_reads_t.Controllers;


import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.UserDto;
import com.example.special_reads_t.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/signUp")
    public String signUpTemplate(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "signUp";
    }

    @PostMapping("/submitForm")
    public ResponseEntity<UserDto> registerUser(@ModelAttribute("userDto") UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            UserDto registeredUserDto = userService.registerUser(userDto);
            return new ResponseEntity<>(registeredUserDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
