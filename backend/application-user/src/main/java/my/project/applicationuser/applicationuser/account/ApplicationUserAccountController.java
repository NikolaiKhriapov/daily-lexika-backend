package my.project.applicationuser.applicationuser.account;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/v1/application-user/account")
@RequiredArgsConstructor
public class ApplicationUserAccountController {

    private final ApplicationUserAccountService applicationUserAccountService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<Response> showAccount() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.showAccount", null, Locale.getDefault()))
                        .data(Map.of("applicationUserDTO", applicationUserAccountService.getApplicationUser()))
                        .build());
    }

    @GetMapping("/photo")
    public ResponseEntity<Response> getApplicationUserPhoto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.getApplicationUserPhoto", null, Locale.getDefault()))
                        .data(Map.of("profilePhoto", applicationUserAccountService.getProfilePhoto()))
                        .build());
    }

    @PostMapping("/photo")
    public ResponseEntity<Response> updateApplicationUserPhoto(@RequestBody MultipartFile file) {
        applicationUserAccountService.updateProfilePhoto(file);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.updateApplicationUserPhoto", null, Locale.getDefault()))
                        .build());
    }

    @DeleteMapping("/photo")
    public ResponseEntity<Response> deleteApplicationUserPhoto() {
        applicationUserAccountService.deleteProfilePhoto();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message(messageSource.getMessage(
                                "response.userAccount.deleteApplicationUserPhoto", null, Locale.getDefault()))
                        .build());
    }
}
