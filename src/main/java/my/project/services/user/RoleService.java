package my.project.services.user;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceAlreadyExistsException;
import my.project.exception.ResourceNotFoundException;
import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.User;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final MessageSource messageSource;

    public void addRoleToUserRoles(User user, RoleName roleName) {
        if (user.getRoleStatistics() == null) {
            user.setRoleStatistics(new HashSet<>());
        }
        throwIfUserAlreadyHasThisRole(user, roleName);
        user.getRoleStatistics().add(new RoleStatistics(roleName));
    }

    public Platform getPlatformByRoleName(RoleName roleName) {
        return switch (roleName) {
            case USER_ENGLISH -> Platform.ENGLISH;
            case USER_CHINESE -> Platform.CHINESE;
            default -> throw new IllegalStateException(
                    messageSource.getMessage("exception.role.invalidRole", null, Locale.getDefault())
                            .formatted(roleName)
            );
        };
    }

    public RoleName getRoleNameByPlatform(Platform platform) {
        return switch (platform) {
            case ENGLISH -> RoleName.USER_ENGLISH;
            case CHINESE -> RoleName.USER_CHINESE;
            case SHARED -> throw new IllegalStateException(
                    messageSource.getMessage("exception.role.invalidPlatform", null, Locale.getDefault())
                            .formatted(platform)
            );
        };
    }

    public RoleStatistics getRoleStatistics() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return user.getRoleStatistics().stream()
                .filter(role -> role.getRoleName().equals(user.getRole()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(messageSource.getMessage(
                        "exception.role.setOfRoleStatisticsDoesNotContainCurrentRole", null, Locale.getDefault())));
    }

    public void throwIfUserNotRegisteredOnPlatform(User user, RoleName roleName) {
        if (!isUserRolesContainsRole(user, roleName)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("exception.authentication.userNotRegisteredOnPlatform", null, Locale.getDefault())
                            .formatted(user.getEmail(), getPlatformByRoleName(roleName))
            );
        }
    }

    private boolean isUserRolesContainsRole(User user, RoleName roleName) {
        List<RoleName> userRoleNames = user.getRoleStatistics().stream().map(RoleStatistics::getRoleName).toList();
        return userRoleNames.contains(roleName);
    }

    private void throwIfUserAlreadyHasThisRole(User user, RoleName roleName) {
        if (isUserRolesContainsRole(user, roleName)) {
            throw new ResourceAlreadyExistsException(
                    messageSource.getMessage("exception.authentication.userAlreadyRegisteredOnPlatform", null, Locale.getDefault())
                            .formatted(user.getEmail(), getPlatformByRoleName(roleName))
            );
        }
    }
}
