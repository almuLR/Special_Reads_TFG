package com.example.special_reads_t.Service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public GeminiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String generateContent(String prompt) {
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textMap = new HashMap<>();
        textMap.put("text", prompt);

        Map<String, Object> partsMap = new HashMap<>();
        partsMap.put("parts", List.of(textMap));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(partsMap));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Realizar la petición POST
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        System.out.println("Respuesta completa: " + response.getBody());
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object candidatesObj = response.getBody().get("candidates");
            if (candidatesObj instanceof List<?> candidates && !candidates.isEmpty()) {
                Object candidateObj = candidates.get(0);
                if (candidateObj instanceof Map<?, ?> candidate) {
                    Object contentObj = candidate.get("content");
                    if (contentObj instanceof Map<?, ?> content) {
                        Object partsObj = content.get("parts");
                        if (partsObj instanceof List<?> parts && !parts.isEmpty()) {
                            Object partObj = parts.get(0);
                            if (partObj instanceof Map<?, ?> part) {
                                Object textObj = part.get("text");
                                if (textObj instanceof String generatedText) {
                                    return generatedText;
                                }
                            }
                        }
                    }
                }
            }
            return "No se encontró el contenido generado en la respuesta";
        }
        return "Error al obtener respuesta de la API de Gemini";
    }
}
