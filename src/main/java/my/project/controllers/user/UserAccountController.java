package my.project.controllers.user;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.user.UserDTO;
import my.project.services.user.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PatchMapping("/info")
    public ResponseEntity<Void> updateUserInfo(@RequestBody UserDTO userDTO) {
        userAccountService.updateUserInfo(userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount() {
        userAccountService.deleteAccount();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
