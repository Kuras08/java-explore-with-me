package ru.practicum.event.model;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> adminSearch(
            List<Long> userIds,
            List<String> states,
            List<Long> categoryIds,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd
    ) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Фильтрация по userIds только если список не содержит 0
            if (userIds != null && !userIds.isEmpty() && !userIds.contains(0L)) {
                predicate = cb.and(predicate, root.get("initiator").get("id").in(userIds));
            }

            if (states != null && !states.isEmpty()) {
                predicate = cb.and(predicate, root.get("state").in(states));
            }

            // Фильтрация по categoryIds только если список не содержит 0
            if (categoryIds != null && !categoryIds.isEmpty() && !categoryIds.contains(0L)) {
                predicate = cb.and(predicate, root.get("category").get("id").in(categoryIds));
            }

            if (rangeStart != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
            return predicate;
        };
    }

    public static Specification<Event> publicSearch(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable
    ) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (text != null && !text.isBlank()) {
                String like = "%" + text.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("annotation")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("title")), like)
                ));
            }

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicate = cb.and(predicate, root.get("category").get("id").in(categoryIds));
            }

            if (paid != null) {
                predicate = cb.and(predicate, cb.equal(root.get("paid"), paid));
            }

            if (rangeStart != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            // Только опубликованные
            predicate = cb.and(predicate, cb.equal(root.get("state"), "PUBLISHED"));

            return predicate;
        };
    }

}

