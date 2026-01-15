package FinanceTracker.controller;

import FinanceTracker.dto.ChangePasswordDto;
import FinanceTracker.dto.UpdateUserDto;
import FinanceTracker.dto.UserResponseDTO;
import FinanceTracker.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(){
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(@RequestBody @Valid UpdateUserDto dto){
        return ResponseEntity.ok(userService.updateProfile(dto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDto dto){
        userService.changePassword(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(){
        userService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }


}
