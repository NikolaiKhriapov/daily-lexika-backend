package my.project.dailylexika.user.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.dailylexika.user.service.RoleService;
import my.project.dailylexika.user.service.UserService;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.user.model.mappers.UserMapper;
import my.project.dailylexika.user.persistence.UserRepository;
import my.project.library.dailylexika.events.user.AccountDeletedEvent;
import my.project.library.dailylexika.events.user.UserEmailUpdatedEvent;
import my.project.library.util.datetime.DateUtil;
import my.project.library.util.exception.BadRequestException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements PublicUserService, UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PublicRoleService publicRoleService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getPage(Pageable pageable) {
        Page<User> pageOfUsers = userRepository.findAll(pageable);
        List<UserDto> listOfUserDto = userMapper.toDtoList(pageOfUsers.getContent());
        return new PageImpl<>(listOfUserDto, pageable, pageOfUsers.getTotalElements());
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDto updateUserInfo(UserDto userDto) {
        User user = getAuthenticatedUser();

        if (!Objects.equals(user.getEmail(), userDto.email())) {
            RoleStatisticsDto currentRole = publicRoleService.getRoleStatistics();
            Platform platform = publicRoleService.getPlatformByRoleName(currentRole.roleName());
            publishUserEmailUpdated(user, platform, userDto.email());
        }

        User updatedUser = userMapper.partialUpdate(userDto, user);
        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        User user = getAuthenticatedUser();

        throwIfPasswordIncorrect(user, request.passwordCurrent());

        user.setPassword(passwordEncoder.encode(request.passwordNew()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteAccount(AccountDeletionRequest request) {
        User user = getAuthenticatedUser();

        throwIfPasswordIncorrect(user, request.passwordCurrent());

        RoleStatistics currentRole = roleService.getRoleStatisticsEntity();
        Platform platform = publicRoleService.getPlatformByRoleName(currentRole.getRoleName());

        user.getRoleStatistics().remove(currentRole);

        boolean isDeleteUser = user.getRoleStatistics().isEmpty();
        if (isDeleteUser) {
            userRepository.delete(user);
        } else {
            userRepository.save(user);
        }

        publishUserDeletedEvent(user, platform, isDeleteUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser() {
        User user = getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.authentication.usernameNotFound")));
    }

    @Override
    @Transactional
    public void updateCurrentStreak(Long newCurrentStreak) {
        User user = getAuthenticatedUser();
        RoleStatistics roleStatistics = roleService.getRoleStatisticsEntity();
        roleStatistics.setCurrentStreak(newCurrentStreak);
        roleStatistics.setDateOfLastStreak(DateUtil.nowInUtc());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateRecordStreak(Long newRecordStreak) {
        User user = getAuthenticatedUser();
        RoleStatistics roleStatistics = roleService.getRoleStatisticsEntity();
        roleStatistics.setRecordStreak(newRecordStreak);
        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void throwIfPasswordIncorrect(User user, String passwordFromRequest) {
        boolean passwordsMatch = passwordEncoder.matches(passwordFromRequest, user.getPassword());
        if (!passwordsMatch) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.authentication.incorrectPassword"));
        }
    }

    private void publishUserDeletedEvent(User user, Platform platform, boolean isDeleteUser) {
        eventPublisher.publishEvent(
                AccountDeletedEvent.builder()
                        .userId(user.getId())
                        .userEmail(user.getEmail())
                        .platform(platform)
                        .isDeleteUser(isDeleteUser)
                        .build()
        );
    }

    private void publishUserEmailUpdated(User user, Platform platform, String emailUpdated) {
        eventPublisher.publishEvent(
                UserEmailUpdatedEvent.builder()
                        .userId(user.getId())
                        .platform(platform)
                        .emailUpdated(emailUpdated)
                        .build()
        );
    }
}
