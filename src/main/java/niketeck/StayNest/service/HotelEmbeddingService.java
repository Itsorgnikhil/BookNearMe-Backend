package niketeck.StayNest.service;

import niketeck.StayNest.entity.Hotel;

public interface HotelEmbeddingService {
    void indexHotel(Hotel hotel);
    void removeHotel(Long hotelId);
}
