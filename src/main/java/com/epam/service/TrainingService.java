package com.epam.service;

import com.epam.model.Training;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;

import java.util.List;

public interface TrainingService {

    Training save(String username, String password, TrainingDtoInput trainingDtoInput);

    List<TrainingForTraineeDtoOutput> findByDateRangeAndTraineeUsername(TrainingTraineeSpecification specification);

    List<TrainingForTrainerDtoOutput> findByDateRangeAndTrainerUsername(
                                                                        TrainingTrainerSpecification specification);

}
