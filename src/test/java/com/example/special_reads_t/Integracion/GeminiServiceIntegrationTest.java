package com.example.special_reads_t.Integracion;

import com.example.special_reads_t.Service.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@TestPropertySource(properties = {
        "gemini.api.url=https://fake-gemini-api.com/api",
        "gemini.api.key=testKey"
})
public class GeminiServiceIntegrationTest {

    @Autowired
    private GeminiService geminiService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        // Obtenemos el RestTemplate interno del geminiService
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(geminiService, "restTemplate");
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void testGenerateContent_Integration() throws Exception {
        // JSON de respuesta simulado
        String jsonResponse = "{\n" +
                "  \"candidates\": [\n" +
                "    {\n" +
                "      \"content\": {\n" +
                "        \"parts\": [\n" +
                "          {\"text\": \"Generated content from gemini\"}\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        // La URL que se construye en el método es: geminiApiUrl + "?key=" + geminiApiKey
        String expectedUrl = "https://fake-gemini-api.com/api?key=testKey";
        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        String result = geminiService.generateContent("test prompt");
        assertEquals("Generated content from gemini", result);

        mockServer.verify();
    }
}