package my.project.admin.user.service;

import my.project.admin.user.model.entities.User;
import my.project.library.admin.dtos.user.UserDto;

public interface UserService {
    UserDto getUser();
    User getUserByEmail(String email);
}
