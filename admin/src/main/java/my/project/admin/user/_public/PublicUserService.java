package my.project.admin.user._public;

import my.project.admin.user.model.entities.User;

public interface PublicUserService {
    User getUserEntityByEmail(String email);
}
