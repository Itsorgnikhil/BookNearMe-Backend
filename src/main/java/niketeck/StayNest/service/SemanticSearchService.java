package niketeck.StayNest.service;

import niketeck.StayNest.dto.HotelPriceResponseDto;
import org.springframework.data.domain.Page;

public interface SemanticSearchService {
    Page<HotelPriceResponseDto> searchHotels(String query, int page, int size);
}
