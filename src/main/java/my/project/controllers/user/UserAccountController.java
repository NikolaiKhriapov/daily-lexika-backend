package my.project.controllers.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.models.dtos.user.PasswordUpdateRequest;
import my.project.models.dtos.user.UserDto;
import my.project.services.user.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo() {
        return ResponseEntity.ok(userAccountService.getUserInfo());
    }

    @PatchMapping("/info")
    public ResponseEntity<UserDto> updateUserInfo(@RequestBody @Valid UserDto userDTO) {
        return ResponseEntity.ok(userAccountService.updateUserInfo(userDTO));

    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        userAccountService.updatePassword(passwordUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount() {
        userAccountService.deleteAccount();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
