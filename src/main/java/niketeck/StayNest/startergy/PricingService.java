package niketeck.StayNest.startergy;

import niketeck.StayNest.entity.Inventory;
import niketeck.StayNest.entity.Room;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PricingService {

    private final PricingStrategy pricingStrategy;

    public PricingService(@Qualifier("holidayPricingStrategy") PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        return pricingStrategy.calculatePrice(room, checkIn, checkOut);
    }

    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        double price = pricingStrategy.calculatePrice(inventory.getRoom(), inventory.getDate(), inventory.getDate());
        return BigDecimal.valueOf(price);
    }

    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}