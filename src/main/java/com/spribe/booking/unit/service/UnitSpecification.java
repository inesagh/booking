package com.spribe.booking.unit.service;

import com.spribe.booking.availability.domain.Availability;
import com.spribe.booking.unit.domain.Unit;
import com.spribe.booking.unit.rest.models.UnitSearchCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UnitSpecification {
    public static Specification<Unit> withCriteria(UnitSearchCriteria criteria) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // Filter by roomsCount
            if (criteria.getRoomsCount() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("roomsCount"), criteria.getRoomsCount()));
            }

            // Filter by type
            if (criteria.getType() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("type"), criteria.getType()));
            }

            // Filter by floor
            if (criteria.getFloor() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("floor"), criteria.getFloor()));
            }

            // Filter by availability flag
            if (criteria.getAvailable() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("available"), criteria.getAvailable()));
            }

            // Filter by price range (using finalPrice)
            if (criteria.getMinPrice() != null) {
                predicate = builder.and(predicate,
                        builder.greaterThanOrEqualTo(root.get("finalPrice"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicate = builder.and(predicate,
                        builder.lessThanOrEqualTo(root.get("finalPrice"), criteria.getMaxPrice()));
            }

            // Filter by availability date range (join with Availability table)
            if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
                // Join availability
                Join<Unit, Availability> availabilityJoin = root.join("availabilityDates", JoinType.INNER);
                Predicate datePredicate = builder.and(
                        builder.lessThanOrEqualTo(availabilityJoin.get("startDate"), criteria.getStartDate()),
                        builder.greaterThanOrEqualTo(availabilityJoin.get("endDate"), criteria.getEndDate()));
                predicate = builder.and(predicate, datePredicate);

                // To avoid duplicates due to join, use distinct
                query.distinct(true);
            }

            return predicate;
        };
    }
}
