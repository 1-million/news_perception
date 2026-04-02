package com.qr.np.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface IAssistant {

    String chat(String userMessage);

    String chat(@MemoryId String memoryId, @UserMessage String userMessage);

}
