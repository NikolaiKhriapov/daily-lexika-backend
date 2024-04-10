package my.project.models.mappers.user;

import my.project.models.dtos.user.RoleStatisticsDto;
import my.project.models.entities.user.RoleStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleStatisticsMapper {

    RoleStatisticsDto toDto(RoleStatistics entity);
}
