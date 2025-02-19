package com.example.special_reads_t.Controllers.REST;

import com.example.special_reads_t.Controllers.ChatGptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatGptService chatGptService;

    @PostMapping(value = "/send", consumes = "application/json")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        String botResponse = chatGptService.getChatResponse(userMessage);
        return ResponseEntity.ok(Collections.singletonMap("response", botResponse));
    }

}
