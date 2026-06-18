package niketeck.StayNest.dto;

import lombok.Data;
import niketeck.StayNest.entity.HotelContactInfo;

@Data
public class HotelDto {
    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
