package my.project.dailybudget.mappers.log;

import my.project.dailybudget.entities.log.Log;
import my.project.library.dailybudget.dtos.log.LogDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LogMapper {

    LogDto toDto(Log entity);

    List<LogDto> toDtoList(List<Log> entity);
}
