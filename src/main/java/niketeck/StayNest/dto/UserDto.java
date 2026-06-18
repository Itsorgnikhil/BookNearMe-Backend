package niketeck.StayNest.dto;
import lombok.Data;
import niketeck.StayNest.entity.enums.Gender;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}
