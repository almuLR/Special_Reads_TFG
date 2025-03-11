package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Service.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeminiServiceUnitTest {

    @Mock
    private RestTemplate restTemplate;

    private GeminiService geminiService;

    @BeforeEach
    public void setUp() {
        // Creamos la instancia de GeminiService usando un RestTemplateBuilder real
        geminiService = new GeminiService(new RestTemplateBuilder());
        // Inyectamos nuestro restTemplate mockeado en el bean (sobrescribiendo el creado en el constructor)
        ReflectionTestUtils.setField(geminiService, "restTemplate", restTemplate);
        // Configuramos valores de propiedades
        ReflectionTestUtils.setField(geminiService, "geminiApiKey", "testKey");
        ReflectionTestUtils.setField(geminiService, "geminiApiUrl", "https://fake-gemini-api.com/api");
    }

    @Test
    public void testGenerateContent_Success() {
        // Creamos la estructura de respuesta simulada:
        // {
        //   "candidates": [
        //      { "content": { "parts": [ { "text": "Generated content from gemini" } ] } }
        //   ]
        // }
        Map<String, Object> part = new HashMap<>();
        part.put("text", "Generated content from gemini");

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> candidate = new HashMap<>();
        candidate.put("content", content);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("candidates", List.of(candidate));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Simulamos la llamada POST
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        String result = geminiService.generateContent("test prompt");
        assertEquals("Generated content from gemini", result);
    }
}