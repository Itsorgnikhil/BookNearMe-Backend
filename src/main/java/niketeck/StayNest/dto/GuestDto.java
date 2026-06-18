package niketeck.StayNest.dto;

import lombok.Data;
import niketeck.StayNest.entity.User;
import niketeck.StayNest.entity.enums.Gender;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class GuestDto {
    private Long id;
    @JsonIgnore
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
