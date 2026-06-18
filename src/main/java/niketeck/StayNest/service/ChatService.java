package niketeck.StayNest.service;

import niketeck.StayNest.dto.ChatRequestDto;
import niketeck.StayNest.dto.ChatResponseDto;

public interface ChatService {
    ChatResponseDto sendMessage(ChatRequestDto request);
}
