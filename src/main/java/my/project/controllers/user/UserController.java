package my.project.controllers.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.models.dtos.user.AccountDeletionRequest;
import my.project.models.dtos.user.PasswordUpdateRequest;
import my.project.models.dtos.user.UserDto;
import my.project.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PatchMapping("/info")
    public ResponseEntity<UserDto> updateUserInfo(@RequestBody @Valid UserDto userDTO) {
        return ResponseEntity.ok(userService.updateUserInfo(userDTO));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        userService.updatePassword(passwordUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@RequestBody @Valid AccountDeletionRequest accountDeletionRequest) {
        userService.deleteAccount(accountDeletionRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
