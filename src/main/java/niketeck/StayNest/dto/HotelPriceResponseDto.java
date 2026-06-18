package niketeck.StayNest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import niketeck.StayNest.entity.HotelContactInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceResponseDto {
    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Double price;
}
