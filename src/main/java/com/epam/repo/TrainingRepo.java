package com.epam.repo;

import com.epam.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingRepo extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {

}