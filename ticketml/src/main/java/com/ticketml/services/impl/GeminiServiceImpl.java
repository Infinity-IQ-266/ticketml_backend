package com.ticketml.services.impl;

import com.ticketml.common.dto.gemini.*;
import com.ticketml.services.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiServiceImpl.class);

    private final WebClient webClient;
    private final String geminiUrl;
    private final String apiKey;

    public GeminiServiceImpl(WebClient webClient,
                             @Value("${gemini.url}") String geminiUrl,
                             @Value("${gemini.api-key}") String apiKey) {
        this.webClient = webClient;
        this.geminiUrl = geminiUrl;
        this.apiKey = apiKey;
    }

    @Override
    public String generateContent(String textPrompt) {
        logger.info("Sending prompt to Gemini: '{}'", textPrompt);

        PartDTO part = new PartDTO(textPrompt);
        ContentDTO content = new ContentDTO(Collections.singletonList(part), "user");
        GeminiRequestDTO requestDTO = new GeminiRequestDTO(Collections.singletonList(content));

        try {
            GeminiResponseDTO response = webClient.post()
                    .uri(geminiUrl)
                    .header("x-goog-api-key", apiKey)
                    .body(Mono.just(requestDTO), GeminiRequestDTO.class)
                    .retrieve()
                    .bodyToMono(GeminiResponseDTO.class)
                    .block(); // .block() để chờ kết quả (phù hợp cho luồng xử lý đồng bộ)

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                String resultText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                logger.info("Received response from Gemini.");
                return resultText;
            } else {
                logger.warn("Received empty or invalid response from Gemini.");
                return "Sorry, I couldn't get a response. Please try again.";
            }
        } catch (Exception e) {
            logger.error("Error calling Gemini API", e);
            return "Sorry, an error occurred while connecting to the AI service.";
        }
    }
}