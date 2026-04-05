package com.qr.np.controller;

import com.qr.np.model.Person;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JSONController {

    @Resource
    private ChatModel chatModel;

    @PostMapping("/json")
    public String json(@RequestBody String prompt) {
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormat.JSON.type()) // type can be either TEXT (default) or JSON
                .jsonSchema(JsonSchema.builder()
                        .name("Person") // OpenAI requires specifying the name for the schema
                        .rootElement(JsonObjectSchema.builder() // see [1] below
                                .addStringProperty("name")
                                .addIntegerProperty("age")
                                .addStringProperty("gender")
                                .required("name", "age", "gender") // see [2] below
                                .build())
                        .build())
                .build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(List.of(UserMessage.from(prompt)))
                .responseFormat(responseFormat)
                .build();
        ChatResponse result = chatModel.chat(chatRequest);
        return result.aiMessage().text();
    }
}
