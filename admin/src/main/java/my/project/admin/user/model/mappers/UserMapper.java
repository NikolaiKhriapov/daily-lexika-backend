package my.project.admin.user.model.mappers;

import my.project.library.admin.dtos.user.UserDto;
import my.project.admin.user.model.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDTO, @MappingTarget User user);
}
