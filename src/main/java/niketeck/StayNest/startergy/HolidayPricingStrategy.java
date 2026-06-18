package niketeck.StayNest.startergy;

import niketeck.StayNest.entity.Room;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component("holidayPricingStrategy")
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy pricingStrategy;

    public HolidayPricingStrategy(@Qualifier("urgencyPricingStrategy") PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        double price = pricingStrategy.calculatePrice(room, checkIn, checkOut);
        boolean hasWeekend = false;
        for (LocalDate date = checkIn; !date.isAfter(checkOut); date = date.plusDays(1)) {
            int dayOfWeek = date.getDayOfWeek().getValue(); // Saturday = 6, Sunday = 7
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                hasWeekend = true;
                break;
            }
        }
        if (hasWeekend) {
            price *= 1.25; // 25% markup if stay includes weekend/holiday
        }
        return price;
    }
}
