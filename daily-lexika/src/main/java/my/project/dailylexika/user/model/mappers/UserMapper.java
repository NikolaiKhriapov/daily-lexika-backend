package my.project.dailylexika.user.model.mappers;

import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.dailylexika.user.model.entities.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RoleStatisticsMapper.class)
public interface UserMapper {

    @Mapping(target = "setOfRoleStatisticsDto", source = "roleStatistics")
    UserDto toDto(User entity);

    List<UserDto> toDtoList(List<User> entityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto dto, @MappingTarget User entity);
}
