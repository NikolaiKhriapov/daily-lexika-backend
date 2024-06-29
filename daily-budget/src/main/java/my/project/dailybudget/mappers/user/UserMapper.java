package my.project.dailybudget.mappers.user;

import my.project.dailybudget.entities.user.User;
import my.project.library.dailybudget.dtos.user.UserDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDTO(User entity);

    List<UserDto> toDtoList(List<User> entityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDTO, @MappingTarget User user);
}
