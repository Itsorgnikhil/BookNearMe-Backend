package niketeck.StayNest.startergy;

import niketeck.StayNest.entity.Room;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component("urgencyPricingStrategy")
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy pricingStrategy;

    public UrgencyPricingStrategy(@Qualifier("searchPricingStrategy") PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        double price = pricingStrategy.calculatePrice(room, checkIn, checkOut);
        LocalDate today = LocalDate.now();
        if (!checkIn.isBefore(today) && checkIn.isBefore(today.plusDays(7))) {
            price *= 1.15; // 15% urgency charge if check-in is within next 7 days
        }
        return price;
    }
}
