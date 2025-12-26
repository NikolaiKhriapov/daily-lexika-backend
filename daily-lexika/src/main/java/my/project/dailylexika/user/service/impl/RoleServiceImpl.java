package my.project.dailylexika.user.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user.model.mappers.RoleStatisticsMapper;
import my.project.dailylexika.user.service.RoleService;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService, PublicRoleService {

    private final RoleStatisticsMapper roleStatisticsMapper;

    @Override
    public RoleName getRoleNameByPlatform(Platform platform) {
        return switch (platform) {
            case ENGLISH -> RoleName.USER_ENGLISH;
            case CHINESE -> RoleName.USER_CHINESE;
        };
    }

    @Override
    public Platform getPlatformByRoleName(RoleName roleName) {
        return switch (roleName) {
            case USER_ENGLISH -> Platform.ENGLISH;
            case USER_CHINESE -> Platform.CHINESE;
        };
    }

    @Override
    @Transactional
    public void addRoleToUserRoles(User user, RoleName roleName) {
        if (user.getRoleStatistics() == null) {
            user.setRoleStatistics(new HashSet<>());
        }
        throwIfUserAlreadyHasThisRole(user, roleName);
        user.getRoleStatistics().add(new RoleStatistics(roleName));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleStatisticsDto getRoleStatistics() {
        RoleStatistics roleStatistics = getRoleStatisticsEntity();
        return roleStatisticsMapper.toDto(roleStatistics);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleStatistics getRoleStatisticsEntity() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getRoleStatistics().stream()
                .filter(role -> role.getRoleName().equals(user.getRole()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(I18nUtil.getMessage("dailylexika-exceptions.role.setOfRoleStatisticsDoesNotContainCurrentRole")));
    }

    @Override
    @Transactional(readOnly = true)
    public void throwIfUserNotRegisteredOnPlatform(User user, RoleName roleName) {
        if (!isUserRolesContainsRole(user, roleName)) {
            throw new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.authentication.userNotRegisteredOnPlatform", user.getEmail(), getPlatformByRoleName(roleName)));
        }
    }

    private boolean isUserRolesContainsRole(User user, RoleName roleName) {
        List<RoleName> userRoleNames = user.getRoleStatistics().stream().map(RoleStatistics::getRoleName).toList();
        return userRoleNames.contains(roleName);
    }

    private void throwIfUserAlreadyHasThisRole(User user, RoleName roleName) {
        if (isUserRolesContainsRole(user, roleName)) {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailylexika-exceptions.authentication.userAlreadyRegisteredOnPlatform", user.getEmail(), getPlatformByRoleName(roleName)));
        }
    }
}
