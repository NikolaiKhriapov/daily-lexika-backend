package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDto> getPage(Pageable pageable);
    void save(User user);
    UserDto updateUserInfo(UserDto userDto);
    void updatePassword(PasswordUpdateRequest request);
    void deleteAccount(AccountDeletionRequest request);
    boolean existsByEmail(String email);
}
