package fr.gwan.parkinglots.domain.converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

/**
 * Contract for a API to Entity converter.
 *
 * @param <A> - API type parameter.
 * @param <E> - Entity type parameter.
 */

public interface EntityConverter<A, E> {

    E toEntity(A api);

    A toApi(E entity);

    default List<E> toEntity(List<A> apiList) {
        if (CollectionUtils.isEmpty(apiList)) {
            return Collections.emptyList();
        }

        return apiList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    default List<A> toApi(List<E> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }

        return entityList.stream().map(this::toApi).filter(Objects::nonNull).collect(Collectors.toList());
    }

    default UUID map(String ref) {
        return ref != null ? UUID.fromString(ref) : null;
    }

    default String map(UUID ref) {
        return ref != null ? ref.toString() : null;
    }
    
    default boolean map(Boolean bool) {
        return bool != null && bool;
    }

}