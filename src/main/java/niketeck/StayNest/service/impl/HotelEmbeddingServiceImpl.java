package niketeck.StayNest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.service.HotelEmbeddingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelEmbeddingServiceImpl implements HotelEmbeddingService {

    private final VectorStore vectorStore;

    @Override
    public void indexHotel(Hotel hotel) {
        log.info("Indexing hotel: {}", hotel.getName());
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("hotelId", hotel.getId());
            metadata.put("city", hotel.getCity());
            metadata.put("name", hotel.getName());

            String amenitiesJoined = hotel.getAmenities() != null ? String.join(", ", hotel.getAmenities()) : "";
            String content = String.format("Hotel: %s, City: %s, Amenities: %s", 
                    hotel.getName(), hotel.getCity(), amenitiesJoined);

            Document doc = new Document(String.valueOf(hotel.getId()), content, metadata);
            vectorStore.add(List.of(doc));
            log.info("Successfully indexed hotel: {}", hotel.getName());
        } catch (Exception e) {
            log.error("Failed to index hotel: {}", hotel.getName(), e);
        }
    }

    @Override
    public void removeHotel(Long hotelId) {
        log.info("Removing hotel from vector store with ID: {}", hotelId);
        try {
            FilterExpressionBuilder builder = new FilterExpressionBuilder();
            Filter.Expression expression = builder.eq("hotelId", hotelId).build();
            vectorStore.delete(expression);
            log.info("Successfully removed hotel with ID: {}", hotelId);
        } catch (Exception e) {
            log.error("Failed to remove hotel with ID: {}", hotelId, e);
        }
    }
}
