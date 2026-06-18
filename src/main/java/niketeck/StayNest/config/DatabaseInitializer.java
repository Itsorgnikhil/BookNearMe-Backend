package niketeck.StayNest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import niketeck.StayNest.entity.User;
import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.entity.HotelContactInfo;
import niketeck.StayNest.entity.Room;
import niketeck.StayNest.entity.enums.Gender;
import niketeck.StayNest.entity.enums.Role;
import niketeck.StayNest.repository.UserRepository;
import niketeck.StayNest.repository.HotelRepository;
import niketeck.StayNest.repository.RoomRepository;
import niketeck.StayNest.service.InventoryService;
import niketeck.StayNest.service.PricingUpdateService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryService inventoryService;
    private final PricingUpdateService pricingUpdateService;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Removing Jaipur reservations and resetting inventories...");
        try {
            jdbcTemplate.execute("DELETE FROM booking_guest WHERE booking_id IN (SELECT b.id FROM booking b JOIN hotel h ON b.hotel_id = h.id WHERE LOWER(TRIM(h.city)) = 'jaipur')");
            jdbcTemplate.execute("DELETE FROM booking WHERE hotel_id IN (SELECT id FROM hotel WHERE LOWER(TRIM(city)) = 'jaipur')");
            jdbcTemplate.execute("UPDATE inventory SET booked_count = 0, reserved_count = 0 WHERE hotel_id IN (SELECT id FROM hotel WHERE LOWER(TRIM(city)) = 'jaipur')");
            log.info("Successfully removed Jaipur reservations and reset inventories.");
        } catch (Exception e) {
            log.error("Failed to clean up Jaipur reservations: {}", e.getMessage(), e);
        }

        String testEmail = "nikhilkmt123@gmail.com";
        User user = userRepository.findByEmail(testEmail).orElse(null);

        if (user == null) {
            log.info("Test user not found, creating a new test user...");
            user = new User();
            user.setEmail(testEmail);
            user.setName("John Developer");
            user.setGender(Gender.MALE);
            user.setDateOfBirth(LocalDate.of(1995, 1, 1));
        } else {
            log.info("Test user found, ensuring credentials and roles are correct...");
        }

        // Always ensure password is BCrypt encoded "password123"
        String rawPassword = "password123";
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Always ensure user has both GUEST and HOTEL_MANAGER roles
        Set<Role> roles = new HashSet<>();
        roles.add(Role.GUEST);
        roles.add(Role.HOTEL_MANAGER);
        user.setRoles(roles);

        userRepository.save(user);
        log.info("Test user successfully initialized/updated with email: {}", testEmail);

        // Create Admin 1
        User admin1 = createAdminIfNotExist("admin1@staynest.com", "Amit Kumar");
        // Create Admin 2
        User admin2 = createAdminIfNotExist("admin2@staynest.com", "Siddharth Shah");
        // Create Admin 3
        User admin3 = createAdminIfNotExist("admin3@staynest.com", "Pooja Sharma");

        // Seed hotels for Admin 1 (Mumbai)
        seedHotelIfNotExist(admin1, "Hotel Taj Mahal Palace", "Mumbai", "Colaba, Mumbai", "taj@staynest.com", "9988776611", new String[]{
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Free Wi-Fi", "Swimming Pool", "Spa & Wellness", "24/7 Room Service"});
        seedHotelIfNotExist(admin1, "Trident Nariman Point", "Mumbai", "Nariman Point, Mumbai", "trident@staynest.com", "9988776612", new String[]{
            "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Free Wi-Fi", "Swimming Pool", "Gym", "Valet Parking"});
        seedHotelIfNotExist(admin1, "The Oberoi Mumbai", "Mumbai", "Marine Drive, Mumbai", "oberoi@staynest.com", "9988776613", new String[]{
            "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Free Wi-Fi", "Spa & Wellness", "Bar", "Ocean View"});

        // Seed hotels for Admin 2 (Goa)
        seedHotelIfNotExist(admin2, "Grand Hyatt Goa", "Goa", "Bambolim, Goa", "hyatt@staynest.com", "9988776621", new String[]{
            "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Private Beach Access", "Swimming Pool", "Beachside Bar", "Breakfast Included"});
        seedHotelIfNotExist(admin2, "W Goa", "Goa", "Vagator Beach, Goa", "wgoa@staynest.com", "9988776622", new String[]{
            "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Private Beach Access", "Swimming Pool", "Nightclub", "Water Sports"});
        seedHotelIfNotExist(admin2, "Cidade de Goa", "Goa", "Vainguinim Beach, Goa", "cidade@staynest.com", "9988776623", new String[]{
            "https://images.unsplash.com/photo-1561501900-3701fa6a0f64?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Swimming Pool", "Breakfast Included", "Free Wi-Fi", "Beach Access"});

        // Seed hotels for Admin 3 (Manali)
        seedHotelIfNotExist(admin3, "The Khyber Himalayan Resort", "Manali", "Solang Valley, Manali", "khyber@staynest.com", "9988776631", new String[]{
            "https://images.unsplash.com/photo-1502784444187-359ac186c5bb?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Fireplace", "Mountain View Balcony", "Free Wi-Fi", "Heated Rooms"});
        seedHotelIfNotExist(admin3, "Span Resort and Spa", "Manali", "Kullu Manali Highway, Manali", "span@staynest.com", "9988776632", new String[]{
            "https://images.unsplash.com/photo-1498503182468-3b51cbb6cb24?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Mountain View Balcony", "Free Wi-Fi", "River View", "Trekking Guide"});
        seedHotelIfNotExist(admin3, "Solang Valley Resort", "Manali", "Solang Valley, Manali", "solang@staynest.com", "9988776633", new String[]{
            "https://images.unsplash.com/photo-1601918774946-25832a4be0d6?auto=format&fit=crop&w=800&q=80"
        }, new String[]{"Mountain View Balcony", "Free Wi-Fi", "Trekking Guide", "Bonfire Area"});
    }

    private User createAdminIfNotExist(String email, String name) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setGender(Gender.MALE);
            user.setDateOfBirth(LocalDate.of(1990, 5, 15));
            user.setPassword(passwordEncoder.encode("password123"));
            Set<Role> roles = new HashSet<>();
            roles.add(Role.GUEST);
            roles.add(Role.HOTEL_MANAGER);
            user.setRoles(roles);
            user = userRepository.save(user);
            log.info("Seeded admin user: {}", email);
        }
        return user;
    }

    private void seedHotelIfNotExist(User owner, String name, String city, String location, String email, String phone, String[] photos, String[] amenities) {
        Hotel hotel = hotelRepository.findByName(name).orElse(null);
        if (hotel == null) {
            hotel = new Hotel();
            hotel.setOwner(owner);
            hotel.setName(name);
            hotel.setCity(city);
            hotel.setPhotos(photos);
            hotel.setAmenities(amenities);
            hotel.setActive(true);

            HotelContactInfo contactInfo = new HotelContactInfo();
            contactInfo.setLocation(location);
            contactInfo.setEmail(email);
            contactInfo.setPhoneNumber(phone);
            contactInfo.setAddress(location);
            hotel.setContactInfo(contactInfo);

            hotel = hotelRepository.save(hotel);
            log.info("Seeded hotel: {}", name);

            // Seed rooms for this hotel
            seedRoomIfNotExist(hotel, "Deluxe Room", BigDecimal.valueOf(6500.00), 10, new String[]{"King Bed", "Mini-bar", "Balcony"});
            seedRoomIfNotExist(hotel, "Executive Suite", BigDecimal.valueOf(14000.00), 5, new String[]{"King Bed", "Living Room", "Bathtub", "Mini-bar"});
            
            // Recalculate prices
            pricingUpdateService.updateHotelPrices(hotel);
        }
    }

    private void seedRoomIfNotExist(Hotel hotel, String type, BigDecimal basePrice, int totalCount, String[] amenities) {
        if (roomRepository.existsByHotelAndType(hotel, type)) {
            log.info("Room of type {} already exists for hotel {}, skipping seeding.", type, hotel.getName());
            return;
        }

        Room room = new Room();
        room.setHotel(hotel);
        room.setType(type);
        room.setBasePrice(basePrice);
        room.setTotalCount(totalCount);
        room.setCapacity(2);
        room.setPhotos(new String[]{
            "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=600&q=80"
        });
        room.setAmenities(amenities);
        room = roomRepository.save(room);
        
        inventoryService.initializeRoomForAYear(room);
    }
}
