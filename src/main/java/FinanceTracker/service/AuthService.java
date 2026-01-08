package FinanceTracker.service;

import FinanceTracker.dto.AuthResponseDTO;
import FinanceTracker.dto.LoginRequest;
import FinanceTracker.dto.RegisterRequest;
import FinanceTracker.entity.RoleEntity;
import FinanceTracker.entity.User;
import FinanceTracker.enums.Role;
import FinanceTracker.mapper.UserMapper;
import FinanceTracker.repository.RoleRepository;
import FinanceTracker.repository.UserRepository;
import FinanceTracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    private AuthResponseDTO register(RegisterRequest request)
    {
        if(userRepository.existsByEmail(request.email()))
        {
            throw new RuntimeException("Email Already Exists");
        }

        if(userRepository.existsByUsername(request.username())){
            throw new RuntimeException("Username Already Exists");
        }

        User user= userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        RoleEntity userRole=roleRepository.findByName(Role.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role Not Found"));
        user.getRoles().add(userRole);

        userRepository.save(user);
        String token=jwtService.generateToken(user);

        return userMapper.toAuthResponse(user,token);
    }


    public AuthResponseDTO login(LoginRequest request){
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    User user=userRepository.findByUsername(request.username())
            .orElseThrow(() -> new RuntimeException("Username Not Found"));

    String token = jwtService.generateToken(user);
    return userMapper.toAuthResponse(user,token);
    }

}
