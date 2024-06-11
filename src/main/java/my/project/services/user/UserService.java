package my.project.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.config.i18n.I18nUtil;
import my.project.models.dtos.user.PasswordUpdateRequest;
import my.project.models.dtos.user.UserDto;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.models.mappers.user.UserMapper;
import my.project.repositories.user.UserRepository;
import my.project.services.flashcards.ReviewService;
import my.project.services.flashcards.WordPackService;
import my.project.services.flashcards.WordService;
import my.project.services.log.LogService;
import my.project.services.notification.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ReviewService reviewService;
    private final WordService wordService;
    private final WordPackService wordPackService;
    private final RoleService roleService;
    private final NotificationService notificationService;
    private final LogService logService;

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(I18nUtil.getMessage("exceptions.authentication.usernameNotFound")));
    }

    public UserDto getUserInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDto updateUserInfo(UserDto userDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User updatedUser = userMapper.partialUpdate(userDTO, user);

        return userMapper.toDTO(userRepository.save(updatedUser));
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean passwordsMatch = passwordEncoder.matches(request.passwordCurrent(), user.getPassword());

        if (passwordsMatch) {
            user.setPassword(passwordEncoder.encode(request.passwordNew()));
            userRepository.save(user);
        } else {
            throw new IllegalStateException("Incorrect password");
        }
    }

    @Transactional
    public void deleteAccount() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RoleStatistics currentRole = roleService.getRoleStatistics();
        Platform platform = roleService.getPlatformByRoleName(currentRole.getRoleName());

        deleteFlashcardsForUserByPlatform(user, platform);
        user.getRoleStatistics().remove(currentRole);

        if (user.getRoleStatistics().isEmpty()) {
            notificationService.deleteAllByUserId(user.getId());
            userRepository.delete(user);
        } else {
            userRepository.save(user);
        }

        logService.logAccountDeletion(user, platform);
    }

    private void deleteFlashcardsForUserByPlatform(User user, Platform platform) {
        reviewService.deleteAllByUserIdAndPlatform(user.getId(), platform);
        wordService.deleteAllByUserIdAndPlatform(user.getId(), platform);
        wordPackService.deleteAllByUserIdAndPlatform(user.getId(), platform);
    }
}
