package my.project.library.util.security;

import my.project.library.admin.enumerations.RoleName;
import org.springframework.stereotype.Component;

@Component("AR")
public final class AdminRoleContainer {

    public static final String SUPER_ADMIN = RoleName.SUPER_ADMIN.toString();
}
