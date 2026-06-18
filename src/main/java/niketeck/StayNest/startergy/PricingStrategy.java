package niketeck.StayNest.startergy;

import niketeck.StayNest.entity.Room;
import java.time.LocalDate;

public interface PricingStrategy {

    double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut);

}
