package niketeck.StayNest.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import niketeck.StayNest.dto.HotelPriceResponseDto;
import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.repository.HotelRepository;
import niketeck.StayNest.service.SemanticSearchService;
import org.modelmapper.ModelMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final VectorStore vectorStore;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<HotelPriceResponseDto> searchHotels(String query, int page, int size) {
        log.info("Performing semantic search for query: {}, page: {}, size: {}", query, page, size);
        
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(20)
                .similarityThreshold(0.5)
                .build();

        List<Document> resultDocuments = vectorStore.similaritySearch(searchRequest);
        log.info("Found {} documents matching query", resultDocuments.size());

        List<Long> hotelIds = resultDocuments.stream()
                .map(doc -> {
                    Object hotelIdObj = doc.getMetadata().get("hotelId");
                    if (hotelIdObj instanceof Number) {
                        return ((Number) hotelIdObj).longValue();
                    } else if (hotelIdObj instanceof String) {
                        return Long.parseLong((String) hotelIdObj);
                    }
                    return null;
                })
                .filter(hotelId -> hotelId != null)
                .distinct()
                .collect(Collectors.toList());

        List<Hotel> hotels = new ArrayList<>();
        if (!hotelIds.isEmpty()) {
            hotels = hotelRepository.findAllById(hotelIds);
        }

        List<HotelPriceResponseDto> responseDtos = hotels.stream()
                .map(hotel -> {
                    HotelPriceResponseDto responseDto = modelMapper.map(hotel, HotelPriceResponseDto.class);
                    responseDto.setPrice(0.0);
                    return responseDto;
                })
                .collect(Collectors.toList());

        // Manual pagination
        int start = Math.min(page * size, responseDtos.size());
        int end = Math.min((page + 1) * size, responseDtos.size());
        List<HotelPriceResponseDto> paginatedList = responseDtos.subList(start, end);

        return new PageImpl<>(paginatedList, PageRequest.of(page, size), responseDtos.size());
    }
}
