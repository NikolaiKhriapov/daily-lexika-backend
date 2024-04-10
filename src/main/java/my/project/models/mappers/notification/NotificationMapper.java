package my.project.models.mappers.notification;

import my.project.models.dtos.notification.NotificationDto;
import my.project.models.entities.notification.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    NotificationDto toDto(Notification entity);

    List<NotificationDto> toDtoList(List<Notification> entity);
}
