package niketeck.StayNest.startergy;

import niketeck.StayNest.entity.Room;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component("basePricingStrategy")
public class BasePricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        long daysCount = ChronoUnit.DAYS.between(checkIn, checkOut) + 1;
        return room.getBasePrice().doubleValue() * daysCount;
    }
}
