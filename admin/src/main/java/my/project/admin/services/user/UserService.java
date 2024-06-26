package my.project.admin.services.user;

import lombok.RequiredArgsConstructor;
import my.project.admin.config.i18n.I18nUtil;
import my.project.library.admin.dtos.user.UserDto;
import my.project.admin.entities.user.User;
import my.project.admin.mappers.user.UserMapper;
import my.project.admin.repositories.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.authentication.usernameNotFound")));
    }

    public UserDto getUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }
}
