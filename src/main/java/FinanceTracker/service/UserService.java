package FinanceTracker.service;

import FinanceTracker.dto.ChangePasswordDto;
import FinanceTracker.dto.UpdateUserDto;
import FinanceTracker.dto.UserResponseDTO;
import FinanceTracker.entity.User;
import FinanceTracker.enums.Role;
import FinanceTracker.mapper.UserMapper;
import FinanceTracker.repository.RoleRepository;
import FinanceTracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

//    @Transactional
//    public void deleteUserByAdmin(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//
//        User currentUser = userRepository.findByUsername(currentUsername)
//                .orElseThrow(() -> new RuntimeException("Current user not found"));
//
//
//        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_ADMIN));
//        if (!isAdmin) {
//            throw new RuntimeException("Access Denied: You can only delete your own account.");
//        }
//        userRepository.delete(user);
//    }

    @Transactional
    public void deleteMyAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDTO updateProfile(UpdateUserDto dto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.username() != null && !user.getUsername().equals(dto.username()) && userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Username already taken");
        }

        if (dto.email() != null && !user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email already taken");
        }

        if (dto.username() != null)
            user.setUsername(dto.username());
        if (dto.email() != null)
            user.setEmail(dto.email());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Transactional
    public void changePassword(ChangePasswordDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if(!passwordEncoder.matches(dto.currentPassword(),user.getPassword()))
        {
            throw new RuntimeException("Wrong current password!");
        }

        if(!dto.newPassword().equals(dto.confirmPassword())){
            throw new RuntimeException("New password do not match with confirm password!");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

    }
}
