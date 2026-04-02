package com.qr.np.store;

import com.qr.np.service.IMemoryService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PgChatMemoryStore implements ChatMemoryStore {

    @Resource
    private IMemoryService memoryService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        log.info("getMessages:{}",memoryId);
        return List.of(UserMessage.from("你好"));
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        log.info("updateMessages:{},{}",memoryId,messages);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        log.info("deleteMessages:{}",memoryId);
    }
}
