package niketeck.StayNest.controller;

import lombok.RequiredArgsConstructor;
import niketeck.StayNest.dto.GeneratedDescriptionDto;
import niketeck.StayNest.service.DescriptionGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class DescriptionController {

    private final DescriptionGeneratorService descriptionGeneratorService;

    @PostMapping("/{hotelId}/generate-description")
    public ResponseEntity<GeneratedDescriptionDto> generateHotelDescription(@PathVariable Long hotelId) {
        GeneratedDescriptionDto response = descriptionGeneratorService.generateHotelDescription(hotelId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{hotelId}/rooms/{roomId}/generate-description")
    public ResponseEntity<GeneratedDescriptionDto> generateRoomDescription(
            @PathVariable Long hotelId,
            @PathVariable Long roomId) {
        GeneratedDescriptionDto response = descriptionGeneratorService.generateRoomDescription(hotelId, roomId);
        return ResponseEntity.ok(response);
    }
}
