package niketeck.StayNest.service.impl;

import lombok.extern.slf4j.Slf4j;
import niketeck.StayNest.dto.GeneratedDescriptionDto;
import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.entity.Room;
import niketeck.StayNest.entity.User;
import niketeck.StayNest.exception.ResourceNotFoundException;
import niketeck.StayNest.exception.UnAuthorisedException;
import niketeck.StayNest.repository.HotelRepository;
import niketeck.StayNest.repository.RoomRepository;
import niketeck.StayNest.service.DescriptionGeneratorService;
import niketeck.StayNest.util.AppUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DescriptionGeneratorServiceImpl implements DescriptionGeneratorService {

    private final ChatClient chatClient;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public DescriptionGeneratorServiceImpl(ChatClient.Builder chatClientBuilder,
                                           HotelRepository hotelRepository,
                                           RoomRepository roomRepository) {
        this.chatClient = chatClientBuilder.build();
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public GeneratedDescriptionDto generateHotelDescription(Long hotelId) {
        log.info("Generating description for hotel ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        User currentUser = AppUtils.getCurrentUser();
        if (!hotel.getOwner().equals(currentUser)) {
            throw new UnAuthorisedException("User does not own this hotel with ID: " + hotelId);
        }

        String promptText = String.format(
                "Write a compelling, warm and professional hotel listing description for a booking platform. " +
                "Hotel name: %s. City: %s. Amenities: %s. Contact: %s. " +
                "Write 3-4 sentences. Make it inviting and highlight unique features. " +
                "Do not include prices. Output only the description text, nothing else.",
                hotel.getName(),
                hotel.getCity(),
                hotel.getAmenities() != null ? String.join(", ", hotel.getAmenities()) : "None",
                hotel.getContactInfo() != null && hotel.getContactInfo().getLocation() != null ? hotel.getContactInfo().getLocation() : "N/A"
        );

        String description = chatClient.prompt()
                .user(promptText)
                .call()
                .content();

        return new GeneratedDescriptionDto(description);
    }

    @Override
    public GeneratedDescriptionDto generateRoomDescription(Long hotelId, Long roomId) {
        log.info("Generating description for room ID: {} in hotel ID: {}", roomId, hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        User currentUser = AppUtils.getCurrentUser();
        if (!hotel.getOwner().equals(currentUser)) {
            throw new UnAuthorisedException("User does not own this hotel with ID: " + hotelId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getHotel().equals(hotel)) {
            throw new ResourceNotFoundException("Room ID " + roomId + " does not belong to hotel ID " + hotelId);
        }

        String promptText = String.format(
                "Write a compelling room description for a hotel booking platform. " +
                "Room type: %s. Hotel: %s in %s. Room amenities: %s. Capacity: %s guests. Base price: %s per night. " +
                "Write 2-3 sentences. Be warm and highlight comfort. Output only the description.",
                room.getType(),
                hotel.getName(),
                hotel.getCity(),
                room.getAmenities() != null ? String.join(", ", room.getAmenities()) : "None",
                room.getCapacity(),
                room.getBasePrice() != null ? room.getBasePrice().toString() : "0"
        );

        String description = chatClient.prompt()
                .user(promptText)
                .call()
                .content();

        return new GeneratedDescriptionDto(description);
    }
}
