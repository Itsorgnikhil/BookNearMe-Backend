package niketeck.StayNest.startergy;


import niketeck.StayNest.entity.Room;
import niketeck.StayNest.entity.Inventory;
import niketeck.StayNest.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
@Component("searchPricingStrategy")
public class SearchPricingStrategy implements PricingStrategy {

    private final PricingStrategy pricingStrategy;
    private final InventoryRepository inventoryRepository;

    public SearchPricingStrategy(
            @Qualifier("occupancyPricingStrategy") PricingStrategy pricingStrategy,
            InventoryRepository inventoryRepository) {
        this.pricingStrategy = pricingStrategy;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public double calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        double price = pricingStrategy.calculatePrice(room, checkIn, checkOut);
        List<Inventory> inventories = inventoryRepository.findByRoomAndDateBetween(room, checkIn, checkOut);
        if (!inventories.isEmpty()) {
            double avgSurgeFactor = inventories.stream()
                    .mapToDouble(inv -> inv.getSurgeFactor().doubleValue())
                    .average()
                    .orElse(1.0);
            price *= avgSurgeFactor;
        }
        return price;
    }
}
