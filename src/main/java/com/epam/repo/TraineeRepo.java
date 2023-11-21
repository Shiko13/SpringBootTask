package com.epam.repo;

import com.epam.model.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepo extends JpaRepository<Trainee, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "trainee-with-users-and-trainers-graph")
    Optional<Trainee> findByUserId(Long userId);

    Optional<Trainee> findByUser_Username(String username);
}
