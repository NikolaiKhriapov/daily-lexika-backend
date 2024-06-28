package my.project.dailylexika.mappers.log;

import my.project.dailylexika.entities.log.Log;
import my.project.library.dailylexika.dtos.log.LogDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LogMapper {

    LogDto toDto(Log entity);

    List<LogDto> toDtoList(List<Log> entity);
}
