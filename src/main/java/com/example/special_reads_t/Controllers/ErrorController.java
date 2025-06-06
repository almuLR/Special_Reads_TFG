package com.example.special_reads_t.Controllers;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("message", "Algo salió mal. Por favor, inténtalo de nuevo.");
        return "error";
    }

    public String getErrorPath(){
        return "/error";
    }
}
