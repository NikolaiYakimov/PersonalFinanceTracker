package FinanceTracker.service;

import FinanceTracker.dto.UserResponseDTO;
import FinanceTracker.entity.RoleEntity;
import FinanceTracker.entity.User;
import FinanceTracker.enums.Role;
import FinanceTracker.mapper.UserMapper;
import FinanceTracker.repository.RoleRepository;
import FinanceTracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream().
                map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponseDTO getUserById(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)){
            throw new RuntimeException("User not found!");
        }
        userRepository.deleteById(userId);
    }

    public void changeUserRole(Long userId,Role newRole)
    {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        RoleEntity roleEntity= roleRepository.findByName(newRole)
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        user.getRoles().clear();
        user.getRoles().add(roleEntity);
        userRepository.save(user);

    }
}
