package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;

public interface RoleService {
    void addRoleToUserRoles(User user, RoleName roleName);
    RoleName getRoleNameByPlatform(Platform platform);
    RoleStatistics getRoleStatisticsEntity();
    void throwIfUserNotRegisteredOnPlatform(User user, RoleName roleName);

}
