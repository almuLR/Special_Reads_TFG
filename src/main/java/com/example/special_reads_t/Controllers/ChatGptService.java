package com.example.special_reads_t.Controllers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ChatGptService {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final WebClient webClient;

    public ChatGptService(WebClient.Builder webClientBuilder) {
        // Se usa la URL base de la API de OpenAI
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    public String getChatResponse(String userMessage) {
        // Preparamos el cuerpo de la solicitud
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "Eres un experto en literatura que responde preguntas sobre libros y autores."));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                Mono<Map> responseMono = this.webClient.post()
                        .uri("/chat/completions")
                        .header("Authorization", "Bearer " + openaiApiKey)
                        .header("Content-Type", "application/json")
                        .body(BodyInserters.fromValue(requestBody))
                        .retrieve()
                        .bodyToMono(Map.class);

                Map<String, Object> responseMap = responseMono.block();
                if (responseMap != null && responseMap.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return ((String) message.get("content")).trim();
                    }
                }
                break; // Solicitud exitosa
            } catch (WebClientResponseException.TooManyRequests ex) {
                retryCount++;
                try {
                    // Espera incremental: 2 segundos * número de reintentos
                    Thread.sleep(2000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return "No se obtuvo respuesta";
    }
}
