package niketeck.StayNest.service;

import jakarta.transaction.Transactional;
import niketeck.StayNest.dto.*;
import niketeck.StayNest.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);


    @Transactional
    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
