package com.qr.np.store;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qr.np.model.Memory;
import com.qr.np.service.IMemoryService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PgChatMemoryStore implements ChatMemoryStore {

    @Resource
    private IMemoryService memoryService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        log.info("getMessages:{}",memoryId);
        List<Memory> memories = memoryService.list(new QueryWrapper<Memory>().eq("session_id",memoryId));
        List<ChatMessage> messages  = memories.stream().map(memory -> UserMessage.from(memory.getText())).collect(Collectors.toUnmodifiableList());
        return messages;
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
