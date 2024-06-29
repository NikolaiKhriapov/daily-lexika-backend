package my.project.dailybudget.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.dailybudget.config.i18n.I18nUtil;
import my.project.dailybudget.entities.user.User;
import my.project.dailybudget.mappers.user.UserMapper;
import my.project.dailybudget.repositories.user.UserRepository;
import my.project.dailybudget.services.log.LogService;
import my.project.library.dailybudget.dtos.user.AccountDeletionRequest;
import my.project.library.dailybudget.dtos.user.PasswordUpdateRequest;
import my.project.library.dailybudget.dtos.user.UserDto;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    public void throwIfUserAlreadyExists(String email) {
        if (existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailybudget-exceptions.authentication.userAlreadyRegistered", email));
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(I18nUtil.getMessage("dailybudget-exceptions.authentication.usernameNotFound")));
    }

    public UserDto getUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDto updateUserInfo(UserDto userDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(user.getEmail(), userDTO.email())) {

            logService.logEmailUpdate(user, userDTO.email());
        }

        User updatedUser = userMapper.partialUpdate(userDTO, user);
        return userMapper.toDTO(userRepository.save(updatedUser));
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        throwIfPasswordIncorrect(user, request.passwordCurrent());

        user.setPassword(passwordEncoder.encode(request.passwordNew()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(AccountDeletionRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        throwIfPasswordIncorrect(user, request.passwordCurrent());

        userRepository.delete(user);

        logService.logAccountDeletion(user);
    }

    public Page<UserDto> getPageOfUsers(Pageable pageable) {
        Page<User> pageOfUsers = userRepository.findAll(pageable);

        List<UserDto> listOfUserDto = userMapper.toDtoList(pageOfUsers.getContent());

        return new PageImpl<>(listOfUserDto, pageable, pageOfUsers.getTotalElements());
    }

    private void throwIfPasswordIncorrect(User user, String passwordFromRequest) {
        boolean passwordsMatch = passwordEncoder.matches(passwordFromRequest, user.getPassword());
        if (!passwordsMatch) {
            throw new IllegalStateException("Incorrect password");
        }
    }
}
