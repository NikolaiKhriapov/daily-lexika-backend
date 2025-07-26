package my.project.dailylexika.user.model.mappers;

import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleStatisticsMapper {

    RoleStatisticsDto toDto(RoleStatistics entity);
}
