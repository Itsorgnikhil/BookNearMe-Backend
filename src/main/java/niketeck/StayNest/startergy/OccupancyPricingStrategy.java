package niketeck.StayNest.startergy;

import niketeck.StayNest.entity.Room;
import niketeck.StayNest.entity.Inventory;
import niketeck.StayNest.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component("occupancyPricingStrategy")
public class OccupancyPricingStrategy implements PricingStrategy {

    private final PricingStrategy pricingStrategy;
    private final InventoryRepository inventoryRepository;

    public OccupancyPricingStrategy(
            @Qualifier("basePricingStrategy") PricingStrategy pricingStrategy,
            InventoryRepository inventoryRepository) {
        this.pricingStrategy = pricingStrategy;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        double price = pricingStrategy.calculatePrice(room, checkIn, checkOut);
        List<Inventory> inventories = inventoryRepository.findByRoomAndDateBetween(room, checkIn, checkOut);
        if (!inventories.isEmpty()) {
            double avgOccupancy = inventories.stream()
                    .mapToDouble(inv -> {
                        if (inv.getTotalCount() == 0) return 0.0;
                        return (double) inv.getBookedCount() / inv.getTotalCount();
                    })
                    .average()
                    .orElse(0.0);
            if (avgOccupancy > 0.8) {
                price *= 1.20; // 20% markup if occupancy is above 80%
            }
        }
        return price;
    }
}
