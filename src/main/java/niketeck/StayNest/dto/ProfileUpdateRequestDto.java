package niketeck.StayNest.dto;

import lombok.Data;
import niketeck.StayNest.entity.enums.Gender;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
