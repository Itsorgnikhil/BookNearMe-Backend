package niketeck.StayNest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemanticSearchRequest {
    private String query;
    private int page = 0;
    private int size = 10;
}
