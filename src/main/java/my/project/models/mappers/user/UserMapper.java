package my.project.models.mappers.user;

import my.project.models.dtos.user.UserDto;
import my.project.models.entities.user.User;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = RoleStatisticsMapper.class
)
public interface UserMapper {

    @Mapping(target = "setOfRoleStatisticsDto", source = "roleStatistics")
    UserDto toDTO(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDTO, @MappingTarget User user);
}
