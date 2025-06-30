package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final GeminiService geminiService;

    public ChatRestController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        String responseMessage = geminiService.generateContent(userMessage);
        Map<String, String> response = new HashMap<>();
        response.put("response", responseMessage);
        return ResponseEntity.ok(response);
    }
}
