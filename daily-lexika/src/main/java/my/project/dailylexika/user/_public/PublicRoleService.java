package my.project.dailylexika.user._public;

import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;

public interface PublicRoleService {
    Platform getPlatformByRoleName(RoleName roleName);
    RoleStatisticsDto getRoleStatistics();
}
