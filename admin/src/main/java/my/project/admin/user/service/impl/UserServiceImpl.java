package my.project.admin.user.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.admin.config.I18nUtil;
import my.project.admin.user._public.PublicUserService;
import my.project.admin.user.service.UserService;
import my.project.library.admin.dtos.user.UserDto;
import my.project.admin.user.model.entities.User;
import my.project.admin.user.model.mappers.UserMapper;
import my.project.admin.user.persistence.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, PublicUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser() {
        User user = getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return getUserEntityByEmail(email);
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.authentication.usernameNotFound")));
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
