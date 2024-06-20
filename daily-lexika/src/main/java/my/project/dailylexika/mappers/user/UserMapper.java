package my.project.dailylexika.mappers.user;

import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.dailylexika.entities.user.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = RoleStatisticsMapper.class
)
public interface UserMapper {

    @Mapping(target = "setOfRoleStatisticsDto", source = "roleStatistics")
    UserDto toDTO(User entity);

    List<UserDto> toDtoList(List<User> entityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDTO, @MappingTarget User user);
}
