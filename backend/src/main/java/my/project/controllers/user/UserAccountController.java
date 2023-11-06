package my.project.controllers.user;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.user.ResponseDTO;
import my.project.models.dto.user.UserDTO;
import my.project.services.user.AuthenticationService;
import my.project.services.user.UserAccountService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> showUserAccount() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.showAccount", null, Locale.getDefault()))
                        .data(Map.of("userDTO", authenticationService.getAuthenticatedUser()))
                        .build());
    }

    @PatchMapping("/info")
    public ResponseEntity<ResponseDTO> updateUserInfo(@RequestBody UserDTO userDTO) {
        userAccountService.updateUserInfo(userDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.updateUserInfo", null, Locale.getDefault()))
                        .build());
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> deleteAccount() {
        userAccountService.deleteAccount();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.deleteAccount", null, Locale.getDefault()))
                        .build());
    }
}
