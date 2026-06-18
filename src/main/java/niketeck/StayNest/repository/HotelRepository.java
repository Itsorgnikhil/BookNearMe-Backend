package niketeck.StayNest.repository;

import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("SELECT h FROM Hotel h JOIN FETCH h.owner WHERE h.owner = :owner")
    List<Hotel> findByOwner(@Param("owner") User user);

    @Query("SELECT h FROM Hotel h JOIN FETCH h.owner WHERE h.active = :active")
    List<Hotel> findByActive(@Param("active") Boolean active);

    java.util.Optional<Hotel> findByName(String name);

    @Query("SELECT h FROM Hotel h JOIN FETCH h.owner WHERE h.city = :city AND h.active = true")
    List<Hotel> findByCityAndActiveTrue(@Param("city") String city);
}
