package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.ErrorMessageConstants;
import com.epam.error.NotFoundException;
import com.epam.mapper.TrainerMapper;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import com.epam.model.User;
import com.epam.model.dto.TrainerDtoInput;
import com.epam.model.dto.TrainerDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerProfileDtoInput;
import com.epam.model.dto.TrainerSaveDtoOutput;
import com.epam.model.dto.TrainerUpdateDtoOutput;
import com.epam.model.dto.UserDtoInput;
import com.epam.repo.TrainerRepo;
import com.epam.repo.TrainingTypeRepo;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepo trainerRepo;

    private final TrainerMapper trainerMapper;

    private final TrainingTypeRepo trainingTypeRepo;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    private AtomicInteger freeActiveTrainers;

    public TrainerServiceImpl(TrainerRepo trainerRepo, TrainerMapper trainerMapper, TrainingTypeRepo trainingTypeRepo,
                              AuthenticationService authenticationService, UserService userService,
                              MeterRegistry meterRegistry) {
        this.trainerRepo = trainerRepo;
        this.trainerMapper = trainerMapper;
        this.trainingTypeRepo = trainingTypeRepo;
        this.authenticationService = authenticationService;
        this.userService = userService;
        freeActiveTrainers = new AtomicInteger();
        this.freeActiveTrainers = meterRegistry.gauge("free-active-trainers", freeActiveTrainers);
    }


    @Override
    @Transactional
    public TrainerSaveDtoOutput save(TrainerDtoInput trainerDtoInput) {
        log.info("save, trainerDtoInput = {}", trainerDtoInput);

        User user = userService.save(new UserDtoInput(trainerDtoInput.getFirstName(), trainerDtoInput.getLastName()));

        TrainingType trainingType = trainingTypeRepo.findById(trainerDtoInput.getSpecialization())
                                                    .orElseThrow(() -> new AccessException(
                                                            ErrorMessageConstants.ACCESS_ERROR_MESSAGE));

        Trainer trainerToSave = trainerMapper.toEntity(trainerDtoInput);
        trainerToSave.setTrainingType(trainingType);
        trainerToSave.setUser(user);

        Trainer trainer = trainerRepo.save(trainerToSave);

        return trainerMapper.toSaveDto(trainer);
    }

    @Override
    public TrainerDtoOutput getByUsername(String username, String password) {
        log.info("getByUserName, username = {}", username);

        User user = getUserByUsername(username);
        authenticate(password, user);

        Trainer trainer = trainerRepo.findByUserId(user.getId())
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));

        return trainerMapper.toDtoOutput(trainer);
    }

    @Override
    @Transactional
    public TrainerUpdateDtoOutput updateProfile(String username, String password,
                                                TrainerProfileDtoInput trainerDtoInput) {
        log.info("updateProfile, username = {}", username);

        User user = getUserByUsername(username);
        authenticate(password, user);

        Trainer trainer = trainerRepo.findByUserId(user.getId())
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));

        if (!trainerDtoInput.getSpecialization().equals(trainer.getTrainingType().getId())) {
            TrainingType trainingType = trainingTypeRepo.findById(trainerDtoInput.getSpecialization())
                                                        .orElseThrow(() -> new AccessException(
                                                                ErrorMessageConstants.ACCESS_ERROR_MESSAGE));
            trainer.setTrainingType(trainingType);
        }

        trainerMapper.updateTrainerProfile(trainer, trainerDtoInput);
        Trainer updatedTrainer = trainerRepo.save(trainer);

        return trainerMapper.toTrainerUpdateDtoOutput(updatedTrainer);
    }

    @Override
    public List<TrainerForTraineeDtoOutput> getTrainersWithEmptyTrainees(String username, String password) {
        log.info("getTrainersWithEmptyTrainees");

        User user = getUserByUsername(username);
        authenticate(password, user);

        List<Trainer> trainers = trainerRepo.findByTraineesIsEmptyAndUserIsActiveTrue();

        if (freeActiveTrainers != null) {
            freeActiveTrainers.set(trainers.size());
        }

        if (trainers.isEmpty()) {
            return new ArrayList<>();
        }

        return trainers.stream().map(trainerMapper::toTrainerForTraineeDtoOutput).toList();
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
