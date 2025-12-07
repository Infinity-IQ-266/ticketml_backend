package com.ticketml.controller;

import com.ticketml.common.dto.chat.ChatRequestDTO;
import com.ticketml.common.dto.chat.ChatResponseDTO;
import com.ticketml.response.Response;
import com.ticketml.services.GeminiService;
import com.ticketml.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public Response handleChatMessage(@Valid @RequestBody ChatRequestDTO request) {
        String googleId = SecurityUtil.getGoogleId();
        String reply = geminiService.generateContent(request.getMessage(), googleId);
        return new Response(new ChatResponseDTO(reply));
    }
}
