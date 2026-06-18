package niketeck.StayNest.controller;

import lombok.RequiredArgsConstructor;
import niketeck.StayNest.dto.ChatRequestDto;
import niketeck.StayNest.dto.ChatResponseDto;
import niketeck.StayNest.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequestDto request) {
        ChatResponseDto response = chatService.sendMessage(request);
        return ResponseEntity.ok(response);
    }
}
