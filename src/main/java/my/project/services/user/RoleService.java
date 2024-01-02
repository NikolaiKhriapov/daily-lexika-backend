package my.project.services.user;

import lombok.RequiredArgsConstructor;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.user.Role;
import my.project.models.entity.user.RoleName;
import my.project.models.entity.user.User;
import my.project.repositories.user.RoleRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final MessageSource messageSource;

    public void addRoleToUserRoles(User user, RoleName roleName) {
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(getOrCreateAndGetRoleByRoleName(roleName));
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
            case CHINESE -> RoleName.USER_CHINESE;
            case ENGLISH -> RoleName.USER_ENGLISH;
        };
    }

    private Role getOrCreateAndGetRoleByRoleName(RoleName roleName) {
        Optional<Role> optionalRole = roleRepository.findByRoleName(roleName);
        return optionalRole.orElseGet(() -> roleRepository.save(new Role(roleName)));
    }
}
