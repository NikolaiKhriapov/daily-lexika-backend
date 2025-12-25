package my.project.dailylexika.user.model.mappers;

import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleStatisticsMapper {

    RoleStatisticsDto toDto(RoleStatistics entity);
}
