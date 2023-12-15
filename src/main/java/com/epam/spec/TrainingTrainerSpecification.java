package com.epam.spec;

import com.epam.model.Training;
import com.epam.spec.filter.TrainingTrainerFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;

@RequiredArgsConstructor
public class TrainingTrainerSpecification implements Specification<Training> {

    private final transient TrainingTrainerFilter filter;

    public static Specification<Training> getTrainerNameSpecification(String username) {
        return (root, query, criteriaBuilder) -> username != null && !username.isEmpty() ?
                criteriaBuilder.like(root.get("trainer").get("user").get("username"), "%" + username + "%") :
                criteriaBuilder.conjunction();
    }

    public static Specification<Training> getPeriodFromSpecification(LocalDate periodFrom) {
        return (root, query, criteriaBuilder) -> periodFrom != null ?
                criteriaBuilder.greaterThanOrEqualTo(root.get("date"), periodFrom) : criteriaBuilder.conjunction();
    }

    public static Specification<Training> getPeriodToSpecification(LocalDate periodTo) {
        return (root, query, criteriaBuilder) -> periodTo != null ?
                criteriaBuilder.lessThanOrEqualTo(root.get("date"), periodTo) : criteriaBuilder.conjunction();
    }

    public static Specification<Training> getTraineeNameSpecification(String traineeName) {
        return (root, query, criteriaBuilder) -> traineeName != null && !traineeName.isEmpty() ?
                criteriaBuilder.like(root.get("trainee").get("user").get("username"), "%" + traineeName + "%") :
                criteriaBuilder.conjunction();
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Training> root, @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        if (filter == null) {
            return criteriaBuilder.conjunction();
        }

        var usernameSpec = getTrainerNameSpecification(filter.getUsername());
        var periodFromSpec = getPeriodFromSpecification(filter.getPeriodFrom());
        var periodToSpec = getPeriodToSpecification(filter.getPeriodTo());
        var traineeNameSpec = getTraineeNameSpecification(filter.getTraineeName());

        return usernameSpec.and(periodFromSpec)
                           .and(periodToSpec)
                           .and(traineeNameSpec)
                           .toPredicate(root, query, criteriaBuilder);
    }
}
