package com.qr.np.controller;

import com.qr.np.dto.ChatRequest;
import com.qr.np.dto.ChatResponse;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChatController {

    @Resource
    private ChatModel chatModel;

    @GetMapping("/")
    public String index() {
        return "chat";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = chatModel.chat(request.getMessage());
        return new ChatResponse(response, true);
    }
}
