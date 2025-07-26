package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;

public interface RoleService {
    RoleName getRoleNameByPlatform(Platform platform);
    void addRoleToUserRoles(User user, RoleName roleName);
    RoleStatistics getRoleStatisticsEntity();
    void throwIfUserNotRegisteredOnPlatform(User user, RoleName roleName);
}
