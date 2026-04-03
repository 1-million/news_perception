package com.qr.np.store;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qr.np.model.Memory;
import com.qr.np.service.IMemoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        List<Memory> memories = memoryService.list(new QueryWrapper<Memory>().eq("session_id",memoryId).orderByAsc("sort"));
        List<ChatMessage> messages  = memories.stream().map(
                memory -> {
                    if(memory.getType().equals("User")) {
                        return UserMessage.from(memory.getText());
                    }
                    if(memory.getType().equals("AI")) {
                        return AiMessage.from(memory.getText());
                    }
                    return null;
                }).collect(Collectors.toList());
        return messages;
    }

    /**
     * 根据类型 分组数据。
     *
     * @param memoryId The ID of the chat memory.
     * @param messages List of messages for the specified chat memory, that represent the current state of the {@link ChatMemory}.
     *                 Can be serialized to JSON using {@link ChatMessageSerializer}.
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        log.info("updateMessages:{},{}",memoryId,messages);
        memoryService.remove(new QueryWrapper<Memory>().eq("session_id",memoryId));
        for (int i = 0;i<messages.size();i++) {
            ChatMessage message = messages.get(i);
            String sMemoryId = String.valueOf(memoryId);
            if(message.type() == ChatMessageType.USER) {
                UserMessage userMessage = (UserMessage) message;
                Memory memory = new Memory(null,sMemoryId,i,"User",userMessage.singleText(),new Date());
                memoryService.save(memory);
            }
            if(message.type() == ChatMessageType.AI) {
                AiMessage aiMessage = (AiMessage) message;
                Memory memory = new Memory(null,sMemoryId,i,"AI",aiMessage.text(),new Date());
                memoryService.save(memory);
            }
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        log.info("deleteMessages:{}",memoryId);
    }
}
