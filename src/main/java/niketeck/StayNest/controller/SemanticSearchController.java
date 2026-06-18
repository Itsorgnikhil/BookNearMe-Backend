package niketeck.StayNest.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import niketeck.StayNest.dto.HotelPriceResponseDto;
import niketeck.StayNest.dto.SemanticSearchRequest;
import niketeck.StayNest.service.SemanticSearchService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;

    @PostMapping("/semantic-search")
    @Operation(summary = "Semantic vector search for hotels", tags = {"Browse Hotels"})
    public ResponseEntity<Page<HotelPriceResponseDto>> semanticSearch(
            @RequestBody SemanticSearchRequest searchRequest) {
        Page<HotelPriceResponseDto> result = semanticSearchService.searchHotels(
                searchRequest.getQuery(),
                searchRequest.getPage(),
                searchRequest.getSize()
        );
        return ResponseEntity.ok(result);
    }
}
