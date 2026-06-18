package niketeck.StayNest.service;

import niketeck.StayNest.dto.GeneratedDescriptionDto;

public interface DescriptionGeneratorService {
    GeneratedDescriptionDto generateHotelDescription(Long hotelId);
    GeneratedDescriptionDto generateRoomDescription(Long hotelId, Long roomId);
}
