package niketeck.StayNest.service;

import lombok.RequiredArgsConstructor;
import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.entity.Room;
import niketeck.StayNest.repository.HotelRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelSearchTools {

    private final HotelRepository hotelRepository;

    @Tool(description = "Search hotels by city name. Returns list of available hotels with name, amenities and base price")
    public String searchHotelsByCity(String city) {
        List<Hotel> hotels = hotelRepository.findByCityAndActiveTrue(city);
        if (hotels.isEmpty()) {
            return "No hotels found in " + city;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %d hotels in %s:\n", hotels.size(), city));
        for (Hotel hotel : hotels) {
            BigDecimal minPrice = hotel.getRooms().stream()
                    .map(Room::getBasePrice)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            String amenities = hotel.getAmenities() != null ? String.join(", ", hotel.getAmenities()) : "None";
            sb.append(String.format("- [ID: %d] %s, Amenities: %s, Base Price: $%s\n",
                    hotel.getId(), hotel.getName(), amenities, minPrice.toString()));
        }
        return sb.toString();
    }

    @Tool(description = "Get hotel details including all rooms, amenities and contact info by hotel ID")
    public String getHotelDetails(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .map(hotel -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Hotel Name: ").append(hotel.getName()).append("\n");
                    sb.append("City: ").append(hotel.getCity()).append("\n");
                    String amenities = hotel.getAmenities() != null ? String.join(", ", hotel.getAmenities()) : "None";
                    sb.append("Amenities: ").append(amenities).append("\n");
                    if (hotel.getContactInfo() != null) {
                        sb.append("Contact Address: ").append(hotel.getContactInfo().getAddress()).append("\n");
                        sb.append("Phone: ").append(hotel.getContactInfo().getPhoneNumber()).append("\n");
                        sb.append("Email: ").append(hotel.getContactInfo().getEmail()).append("\n");
                    }
                    sb.append("Rooms:\n");
                    if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
                        for (Room room : hotel.getRooms()) {
                            sb.append(String.format("  - Room ID: %d, Type: %s, Base Price: $%s, Capacity: %d, Total Count: %d\n",
                                    room.getId(), room.getType(), room.getBasePrice().toString(), room.getCapacity(), room.getTotalCount()));
                        }
                    } else {
                        sb.append("  - No rooms registered.\n");
                    }
                    return sb.toString();
                })
                .orElse("Hotel not found with ID: " + hotelId);
    }

    @Tool(description = "Search hotels by amenity like pool, wifi, parking, gym")
    public String searchHotelsByAmenity(String amenity) {
        List<Hotel> activeHotels = hotelRepository.findByActive(true);
        List<Hotel> matched = activeHotels.stream()
                .filter(h -> h.getAmenities() != null && Arrays.stream(h.getAmenities())
                        .anyMatch(a -> a.equalsIgnoreCase(amenity)))
                .collect(Collectors.toList());

        if (matched.isEmpty()) {
            return "No hotels found with amenity: " + amenity;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %d hotels with amenity '%s':\n", matched.size(), amenity));
        for (Hotel hotel : matched) {
            BigDecimal minPrice = hotel.getRooms().stream()
                    .map(Room::getBasePrice)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            sb.append(String.format("- [ID: %d] %s in %s, Base Price: $%s\n",
                    hotel.getId(), hotel.getName(), hotel.getCity(), minPrice.toString()));
        }
        return sb.toString();
    }

    @Tool(description = "Get available cities where hotels exist on the platform")
    public String getAvailableCities() {
        List<Hotel> activeHotels = hotelRepository.findByActive(true);
        List<String> cities = activeHotels.stream()
                .map(Hotel::getCity)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (cities.isEmpty()) {
            return "No cities with active hotels available.";
        }
        return "Available cities: " + String.join(", ", cities);
    }
}
