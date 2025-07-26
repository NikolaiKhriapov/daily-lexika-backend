package my.project.dailylexika.notification.model.mappers;

import my.project.library.dailylexika.dtos.notification.NotificationDto;
import my.project.dailylexika.notification.model.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    NotificationDto toDto(Notification entity);
    List<NotificationDto> toDtoList(List<Notification> entity);
}
