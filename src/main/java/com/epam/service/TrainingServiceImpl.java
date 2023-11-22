package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.ErrorMessageConstants;
import com.epam.error.NotFoundException;
import com.epam.mapper.TrainingMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.User;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.repo.TraineeRepo;
import com.epam.repo.TrainerRepo;
import com.epam.repo.TrainingRepo;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepo trainingRepo;

    private final TraineeRepo traineeRepo;

    private final TrainerRepo trainerRepo;

    private final TrainingMapper trainingMapper;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    @Override
    @Transactional
    public Training save(String username, String password, TrainingDtoInput trainingDtoInput) {
        log.info("save, trainingDtoInput = {}", trainingDtoInput);

        User user = getUserByUsername(username);
        authenticate(password, user);

        Training trainingToSave = trainingMapper.toEntity(trainingDtoInput);
        Trainee trainee = traineeRepo.findByUser_Username(trainingDtoInput.getTraineeUsername())
                                     .orElseThrow(
                                             () -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));
        Trainer trainer = trainerRepo.findByUser_Username(trainingDtoInput.getTrainerUsername())
                                     .orElseThrow(
                                             () -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));

        trainingToSave.setTrainee(trainee);
        trainingToSave.setTrainer(trainer);

        return trainingRepo.save(trainingToSave);
    }

    @Override
    @Timed("findByDateRangeAndTraineeUsernameTime")
    public List<TrainingForTraineeDtoOutput> findByDateRangeAndTraineeUsername(
            TrainingTraineeSpecification specification) {

        log.info("findByDateRangeAndTraineeUsername, specification = {}", specification);

        List<Training> trainings = trainingRepo.findAll(specification);

        return trainingMapper.toTrainingForTraineeDtoList(trainings);
    }

    @Override
    @Timed("findByDateRangeAndTrainerUsernameTime")
    public List<TrainingForTrainerDtoOutput> findByDateRangeAndTrainerUsername(
            TrainingTrainerSpecification specification) {
        log.info("findByDateRangeAndTrainerUsername, specification = {}", specification);

        List<Training> trainings = trainingRepo.findAll(specification);

        return trainingMapper.toTrainingForTrainerDtoList(trainings);
    }

    private User getUserByUsername(String username) {
        return userService.findUserByUsername(username)
                          .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));
    }

    public void authenticate(String password, User user) {
        if (authenticationService.checkAccess(password, user)) {
            throw new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE);
        }
    }
}
