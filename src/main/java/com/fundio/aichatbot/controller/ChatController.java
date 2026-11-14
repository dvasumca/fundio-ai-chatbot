package com.fundio.aichatbot.controller;

import com.fundio.aichatbot.dto.ChatRequest;
import com.fundio.aichatbot.dto.ChatResponse;
import com.fundio.aichatbot.service.ChatService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String answer =
                chatService.getResponse(request.message());

        return new ChatResponse(answer);
    }
}