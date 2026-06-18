package niketeck.StayNest.service;

import niketeck.StayNest.dto.HotelDto;
import niketeck.StayNest.dto.HotelInfoDto;
import niketeck.StayNest.dto.HotelInfoRequestDto;

import java.util.List;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto);

    List<HotelDto> getAllHotels();

    List<HotelDto> getAllActiveHotels();
}
