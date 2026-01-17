package FinanceTracker.service;

import FinanceTracker.dto.AuthResponseDTO;
import FinanceTracker.dto.RegisterRequest;
import FinanceTracker.entity.RoleEntity;
import FinanceTracker.entity.User;
import FinanceTracker.enums.Role;
import FinanceTracker.mapper.UserMapper;
import FinanceTracker.repository.RoleRepository;
import FinanceTracker.repository.UserRepository;
import FinanceTracker.security.JwtService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;


    @Test
    void register_ShouldRegisterUser_WhenRequestIsValid(){
        RegisterRequest request=new RegisterRequest("ivan","ivan@email.com","Pass123!");

        RoleEntity roleEntity=new RoleEntity();
        roleEntity.setName(Role.ROLE_USER);

        User savedUser=new User();
        savedUser.setId(1L);
        savedUser.setUsername("ivan");
        savedUser.setRoles(new java.util.HashSet<>());

        AuthResponseDTO expectedResponse = new AuthResponseDTO("mock-jwt-token", "ivan", Role.ROLE_USER);

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(savedUser); // Мапърът връща entity
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
        when(roleRepository.findByName(Role.ROLE_USER)).thenReturn(Optional.of(roleEntity));
        when(jwtService.generateToken(savedUser)).thenReturn("mock-jwt-token");
        when(userMapper.toAuthResponse(savedUser,"mock-jwt-token")).thenReturn(expectedResponse);

        AuthResponseDTO response=authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.token());
        assertEquals("ivan", response.username());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        RegisterRequest request = new RegisterRequest("existing", "mail@mail.com", "pass");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));


        verify(userRepository, never()).save(any());
    }
}
