package my.project.dailylexika.mappers.user;

import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.dailylexika.entities.user.RoleStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleStatisticsMapper {

    RoleStatisticsDto toDto(RoleStatistics entity);
}
