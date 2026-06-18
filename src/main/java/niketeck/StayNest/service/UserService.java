package niketeck.StayNest.service;

import niketeck.StayNest.dto.ProfileUpdateRequestDto;
import niketeck.StayNest.dto.UserDto;
import niketeck.StayNest.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
