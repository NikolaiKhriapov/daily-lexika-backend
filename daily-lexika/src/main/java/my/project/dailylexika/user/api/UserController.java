package my.project.dailylexika.user.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.dailylexika.user.service.UserService;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PublicUserService publicUserService;

    @GetMapping("/info")
    public ResponseEntity<UserDto> getUser() {
        return ResponseEntity.ok(publicUserService.getUser());
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

    @GetMapping
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<Page<UserDto>> getPageOfUsers(@RequestParam("page") int page,
                                                        @RequestParam("size") int size,
                                                        @RequestParam(name = "sort", defaultValue = "desc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(Sort.Direction.DESC, "id")
                : Sort.by(Sort.Direction.ASC, "id");
        return ResponseEntity.ok(userService.getPage(PageRequest.of(page, size, sort)));
    }
}
