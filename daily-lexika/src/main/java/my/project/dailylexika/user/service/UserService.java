package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDto> getPage(Pageable pageable);
    void save(User user);
    UserDto updateUserInfo(@Valid UserDto userDto);
    void updatePassword(@Valid PasswordUpdateRequest request);
    void deleteAccount(@Valid AccountDeletionRequest request);
    boolean existsByEmail(@NotBlank String email);
}
