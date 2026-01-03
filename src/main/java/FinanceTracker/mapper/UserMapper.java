package FinanceTracker.mapper;

import FinanceTracker.dto.AuthResponseDTO;
import FinanceTracker.dto.RegisterRequest;
import FinanceTracker.dto.UserResponseDTO;
import FinanceTracker.entity.RoleEntity;
import FinanceTracker.entity.User;
import FinanceTracker.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public AuthResponseDTO toAuthResponse(User user, String token) {
        if (user == null) {
            return null;
        }

        Role roleEnum = user.getRoles().stream()
                .findFirst().
                map(RoleEntity::getName)
                .orElse(Role.ROLE_USER);
        return new AuthResponseDTO(
                token,
                user.getUsername(),
                roleEnum

                );
    }

    public User toEntity(RegisterRequest dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        //Service need to encode this
        user.setPassword(dto.password());
        return user;
    }


}
