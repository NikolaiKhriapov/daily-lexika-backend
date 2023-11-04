package my.project.models.mapper.notification;

import java.util.List;
import java.util.stream.Collectors;

interface Mapper<E, D> {

    D toDTO(E entity);

    default E toEntity(D dto) {
        throw new UnsupportedOperationException("toEntity conversion is not supported");
    }

    default List<D> toDTOList(List<E> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    default List<E> toEntityList(List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
