package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;

import jakarta.validation.constraints.NotNull;

public interface RoleService {
    RoleName getRoleNameByPlatform(@NotNull Platform platform);
    void addRoleToUserRoles(@NotNull User user, @NotNull RoleName roleName);
    RoleStatistics getRoleStatisticsEntity();
    void throwIfUserNotRegisteredOnPlatform(@NotNull User user, @NotNull RoleName roleName);
}
