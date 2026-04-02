package com.qr.np.config;

import com.qr.np.service.IAssistant;
import com.qr.np.service.IMemoryService;
import com.qr.np.store.PgChatMemoryStore;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.http.client.HttpClientBuilder;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class AIConfig {

    @Resource
    private PgChatMemoryStore chatMemoryStore;

    @Value("${langchain4j.open-ai.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.temperature}")
    private Double temperature;

    @Bean("chatModel")
    public ChatModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public StreamingChatModel streamingChatLanguageModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
    }

    // 创建一个AI服务。
    @Bean
    public IAssistant getChatService() {
        ChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .timeout(Duration.ofSeconds(100))
                .build();

//        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .maxMessages(100)
//                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryStore(chatMemoryStore)
                .maxMessages(100)
                .build();

        WebSearchContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(SearXNGWebSearchEngine.builder()
                        .baseUrl("http://111.228.34.65:8888")
                        .build())
                .build();

        QueryRouter queryRouter = new DefaultQueryRouter(webSearchContentRetriever);

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        // 工具
        Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
        ToolSpecification screenHostTool = ToolSpecification.builder()
                .name("screen-host")
                .description("屏幕截图工具。")
                .parameters(JsonObjectSchema.builder()
                        .addIntegerProperty("left","左上角x坐标")
                        .addIntegerProperty("top","左上角y坐标")
                        .addIntegerProperty("width","宽度")
                        .addIntegerProperty("height","高度")
                        .required("left","top","width","height")
                .build())
                .build();
        tools.put(screenHostTool, (request, memoryId)->{
            return "截图成功";
        });
        return AiServices
                .builder(IAssistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                // 设置 WebSearchTools
                //.retrievalAugmentor(retrievalAugmentor)
                .tools(tools)
                .build();
    }
}
