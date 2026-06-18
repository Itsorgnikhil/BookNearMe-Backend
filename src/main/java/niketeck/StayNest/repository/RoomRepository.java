package niketeck.StayNest.repository;

import niketeck.StayNest.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByHotelAndType(niketeck.StayNest.entity.Hotel hotel, String type);
}
