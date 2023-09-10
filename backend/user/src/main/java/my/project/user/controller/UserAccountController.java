package my.project.user.controller;

import lombok.RequiredArgsConstructor;
import my.project.user.model.dto.ResponseDTO;
import my.project.user.service.UserAccountService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
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
                        .data(Map.of("userDTO", userAccountService.getUser()))
                        .build());
    }

    @GetMapping("/photo")
    public ResponseEntity<ResponseDTO> getUserPhoto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.getUserPhoto", null, Locale.getDefault()))
                        .data(Map.of("profilePhoto", userAccountService.getProfilePhoto()))
                        .build());
    }

    @PostMapping("/photo")
    public ResponseEntity<ResponseDTO> updateUserPhoto(@RequestBody MultipartFile file) {
        userAccountService.updateProfilePhoto(file);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.updateUserPhoto", null, Locale.getDefault()))
                        .build());
    }

    @DeleteMapping("/photo")
    public ResponseEntity<ResponseDTO> deleteUserPhoto() {
        userAccountService.deleteProfilePhoto();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.deleteUserPhoto", null, Locale.getDefault()))
                        .build());
    }
}
