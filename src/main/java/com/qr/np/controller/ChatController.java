package com.qr.np.controller;

import com.qr.np.model.ChatRequest;
import com.qr.np.model.ResultResponse;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    @Resource
    private ChatModel chatModel;

    private final List<ChatMessage> memorys = new ArrayList<>();

    private final ChatMemory chatMemories = MessageWindowChatMemory.withMaxMessages(100);

    @GetMapping("/")
    public String index() {
        return "chat";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ResultResponse chat(@RequestBody ChatRequest request) {
        String response = chatModel.chat(request.getMessage());
        return new ResultResponse(response, true);
    }

    @PostMapping("/api/memChat")
    @ResponseBody
    public ResultResponse memChat(@RequestBody ChatRequest request) {
        memorys.add(UserMessage.userMessage(request.getMessage()));
        ChatResponse response = chatModel.chat(memorys);
        memorys.add(UserMessage.userMessage(response.aiMessage().text()));
        return new ResultResponse(response.aiMessage().text(), true);
    }

    @PostMapping("/api/memChatV2")
    @ResponseBody
    public ResultResponse memChatV2(@RequestBody ChatRequest request) {
        chatMemories.add(UserMessage.userMessage(request.getMessage()));
        ChatResponse response = chatModel.chat(chatMemories.messages());
        chatMemories.add(UserMessage.userMessage(response.aiMessage().text()));
        return new ResultResponse(response.aiMessage().text(), true);
    }
}
