package niketeck.StayNest.service.impl;

import lombok.extern.slf4j.Slf4j;
import niketeck.StayNest.dto.ChatRequestDto;
import niketeck.StayNest.dto.ChatResponseDto;
import niketeck.StayNest.repository.HotelRepository;
import niketeck.StayNest.repository.InventoryRepository;
import niketeck.StayNest.service.ChatService;
import niketeck.StayNest.service.HotelSearchTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final ConcurrentHashMap<String, List<Message>> chatHistory = new ConcurrentHashMap<>();

    public ChatServiceImpl(ChatClient.Builder chatClientBuilder,
                           HotelSearchTools hotelSearchTools,
                           HotelRepository hotelRepository,
                           InventoryRepository inventoryRepository) {
        this.hotelRepository = hotelRepository;
        this.inventoryRepository = inventoryRepository;
        this.chatClient = chatClientBuilder
                .defaultSystem("You are a helpful travel assistant for StayEase hotel booking platform. " +
                        "You help users find hotels, answer questions about amenities, pricing, and " +
                        "availability. You have access to tools to search the real hotel database. " +
                        "Be friendly, concise, and helpful. Always suggest booking if a hotel matches.")
                .defaultTools(hotelSearchTools)
                .build();
    }

    @Override
    public ChatResponseDto sendMessage(ChatRequestDto request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        List<Message> history = chatHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        synchronized (history) {
            history.add(new UserMessage(request.getMessage()));

            // Keep last 10 messages (including new user message)
            if (history.size() > 10) {
                history.subList(0, history.size() - 10).clear();
            }

            log.info("Sending message to chatbot for session: {}. History size: {}", sessionId, history.size());

            String reply = chatClient.prompt()
                    .messages(history)
                    .call()
                    .content();

            history.add(new AssistantMessage(reply));

            // Keep last 10 messages after adding reply
            if (history.size() > 10) {
                history.subList(0, history.size() - 10).clear();
            }

            return new ChatResponseDto(reply, sessionId);
        }
    }
}
