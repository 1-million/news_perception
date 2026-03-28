package com.qr.np.controller;

import com.qr.np.model.RequestMsg;
import com.qr.np.model.ResultResponse;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.*;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import dev.langchain4j.web.search.WebSearchResults;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class ChatController {

    @Resource
    private ChatModel chatModel;
    @Resource
    private StreamingChatModel streamingChatModel;

    private final List<ChatMessage> memorys = new ArrayList<>();

    private final ChatMemory chatMemories = MessageWindowChatMemory.withMaxMessages(100);

    @Resource
    private SearchApiWebSearchEngine searchApiWebSearchEngine;

    @GetMapping("/")
    public String index() {
        return "chat";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ResultResponse chat(@RequestBody RequestMsg request) {
        String response = chatModel.chat(request.getMessage());
        return new ResultResponse(response, true);
    }

    @PostMapping("/api/streamChat")
    @ResponseBody
    public ResultResponse streamChat(@RequestBody RequestMsg request) {
        streamingChatModel.chat(request.getMessage(),new StreamingChatResponseHandler(){
            @Override
            public void onPartialResponse(String partialResponse) {
                StreamingChatResponseHandler.super.onPartialResponse(partialResponse);
                System.out.println("onPartialResponse:"+partialResponse);
            }

            @Override
            public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
                StreamingChatResponseHandler.super.onPartialResponse(partialResponse, context);
                System.out.println("onPartialResponse:"+partialResponse+","+context);
            }

            @Override
            public void onPartialThinking(PartialThinking partialThinking) {
                StreamingChatResponseHandler.super.onPartialThinking(partialThinking);
                System.out.println("onPartialThinking:"+partialThinking);
            }

            @Override
            public void onPartialThinking(PartialThinking partialThinking, PartialThinkingContext context) {
                StreamingChatResponseHandler.super.onPartialThinking(partialThinking, context);
                System.out.println("onPartialThinking:"+partialThinking+","+context);
            }

            @Override
            public void onPartialToolCall(PartialToolCall partialToolCall) {
                StreamingChatResponseHandler.super.onPartialToolCall(partialToolCall);
            }

            @Override
            public void onPartialToolCall(PartialToolCall partialToolCall, PartialToolCallContext context) {
                StreamingChatResponseHandler.super.onPartialToolCall(partialToolCall, context);
            }

            @Override
            public void onCompleteToolCall(CompleteToolCall completeToolCall) {
                StreamingChatResponseHandler.super.onCompleteToolCall(completeToolCall);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("onCompleteResponse:"+completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                System.out.println("onError:"+error);
            }
        });
        return new ResultResponse(null, true);
    }

    @PostMapping("/api/memChat")
    @ResponseBody
    public ResultResponse memChat(@RequestBody RequestMsg request) {
        memorys.add(UserMessage.userMessage(request.getMessage()));
        ChatResponse response = chatModel.chat(memorys);
        memorys.add(UserMessage.userMessage(response.aiMessage().text()));
        return new ResultResponse(response.aiMessage().text(), true);
    }

    @PostMapping("/api/memChatV2")
    @ResponseBody
    public ResultResponse memChatV2(@RequestBody RequestMsg request) {
        chatMemories.add(UserMessage.userMessage(request.getMessage()));
        ChatResponse response = chatModel.chat(chatMemories.messages());
        chatMemories.add(UserMessage.userMessage(response.aiMessage().text()));
        return new ResultResponse(response.aiMessage().text(), true);
    }

    @PostMapping("/api/searchChat")
    @ResponseBody
    public ResultResponse searchChat(@RequestBody RequestMsg request) {
        System.out.println("searchApiWebSearchEngine:"+searchApiWebSearchEngine);
        WebSearchResults results = searchApiWebSearchEngine.search(request.getMessage());
        System.out.println("searchChat:"+results);
        List<ChatMessage> searchMessages = new ArrayList<>();
        for(WebSearchOrganicResult result:results.results()){
            if(Objects.nonNull(result.title())) {
                searchMessages.add(UserMessage.userMessage(result.title()));
            }
            if(Objects.nonNull(result.url())) {
                searchMessages.add(UserMessage.userMessage(result.url().toString()));
            }
            if(Objects.nonNull(result.snippet())) {
                searchMessages.add(UserMessage.userMessage(result.snippet()));
            }
        }
        searchMessages.add(UserMessage.userMessage("以上是搜索结果，请总结搜索内容后返回。"));
        ChatResponse response = chatModel.chat(searchMessages);
        return new ResultResponse(response.aiMessage().text(), true);
    }
}
