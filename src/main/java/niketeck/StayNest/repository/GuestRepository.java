package niketeck.StayNest.repository;

import niketeck.StayNest.entity.Guest;
import niketeck.StayNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest,Long> {
    List<Guest> findByUser(User user);
}
