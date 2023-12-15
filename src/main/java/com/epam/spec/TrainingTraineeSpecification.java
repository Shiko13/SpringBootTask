package com.epam.spec;

import com.epam.model.Training;
import com.epam.spec.filter.TrainingTraineeFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;

@RequiredArgsConstructor
public class TrainingTraineeSpecification implements Specification<Training> {

    private final transient TrainingTraineeFilter filter;

    public static Specification<Training> getTraineeNameSpecification(String username) {
        return (root, query, criteriaBuilder) -> username != null && !username.isEmpty() ?
                criteriaBuilder.like(root.get("trainee").get("user").get("username"), "%" + username + "%") :
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

    public static Specification<Training> getTrainerNameSpecification(String trainerName) {
        return (root, query, criteriaBuilder) -> trainerName != null && !trainerName.isEmpty() ?
                criteriaBuilder.like(root.get("trainer").get("user").get("username"), "%" + trainerName + "%") :
                criteriaBuilder.conjunction();
    }

    public static Specification<Training> getTrainingTypeSpecification(String trainingType) {
        return (root, query, criteriaBuilder) -> trainingType != null && !trainingType.isEmpty() ?
                criteriaBuilder.equal(root.get("trainingType").get("name"), trainingType) :
                criteriaBuilder.conjunction();
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Training> root, @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        if (filter == null) {
            return criteriaBuilder.conjunction();
        }

        var usernameSpec = getTraineeNameSpecification(filter.getUsername());
        var periodFromSpec = getPeriodFromSpecification(filter.getPeriodFrom());
        var periodToSpec = getPeriodToSpecification(filter.getPeriodTo());
        var trainerNameSpec = getTrainerNameSpecification(filter.getTrainerName());
        var trainingTypeSpec = getTrainingTypeSpecification(filter.getTrainingType());

        return usernameSpec.and(periodFromSpec)
                           .and(periodToSpec)
                           .and(trainerNameSpec)
                           .and(trainingTypeSpec)
                           .toPredicate(root, query, criteriaBuilder);
    }
}
