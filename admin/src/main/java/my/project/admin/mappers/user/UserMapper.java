package my.project.admin.mappers.user;

import my.project.library.admin.dtos.user.UserDto;
import my.project.admin.entities.user.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDTO(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDTO, @MappingTarget User user);
}
