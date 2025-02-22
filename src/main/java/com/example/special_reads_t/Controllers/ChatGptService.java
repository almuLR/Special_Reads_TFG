package com.example.special_reads_t.Controllers;


import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
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

    public String getChatResponse(String userMessage) {
        try {
            OpenAiService service = new OpenAiService(openaiApiKey, 60);

            List<ChatMessage> messages = List.of(
                    new ChatMessage("system", "Eres un experto en literatura que responde preguntas sobre libros y autores."),
                    new ChatMessage("user", userMessage)
            );

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .build();

            var response = service.createChatCompletion(request);
            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent().trim();
            }
        } catch (Exception ex) {
            // Aquí registramos el error y devolvemos un mensaje de error
            System.err.println("Error al llamar a OpenAI: " + ex.getMessage());
            // O puedes usar un logger
            return "Error al obtener respuesta (" + ex.getMessage() + ")";
        }
        return "No se obtuvo respuesta";
    }
}
