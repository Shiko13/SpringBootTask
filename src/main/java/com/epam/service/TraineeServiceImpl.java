package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.ErrorMessageConstants;
import com.epam.error.NotFoundException;
import com.epam.mapper.TraineeMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.User;
import com.epam.model.dto.TraineeDtoInput;
import com.epam.model.dto.TraineeDtoOutput;
import com.epam.model.dto.TraineeProfileDtoInput;
import com.epam.model.dto.TraineeSaveDtoOutput;
import com.epam.model.dto.TraineeUpdateDtoOutput;
import com.epam.model.dto.TraineeUpdateListDtoOutput;
import com.epam.model.dto.TrainerShortDtoInput;
import com.epam.model.dto.UserDtoInput;
import com.epam.repo.TraineeRepo;
import com.epam.repo.TrainerRepo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepo traineeRepo;

    private final TrainerRepo trainerRepo;

    private final TraineeMapper traineeMapper;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    private final Counter traineeRegistrationCounter;

    public TraineeServiceImpl(TraineeRepo traineeRepo, TrainerRepo trainerRepo, TraineeMapper traineeMapper,
                              AuthenticationService authenticationService, UserService userService, MeterRegistry meterRegistry) {
        this.traineeRepo = traineeRepo;
        this.trainerRepo = trainerRepo;
        this.traineeMapper = traineeMapper;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.traineeRegistrationCounter = meterRegistry.counter("trainee_registration");
    }

    @Override
    @Transactional
    public TraineeSaveDtoOutput save(TraineeDtoInput traineeDtoInput) {
        log.info("save, traineeDtoInput = {}", traineeDtoInput);

        User user = userService.save(new UserDtoInput(traineeDtoInput.getFirstName(), traineeDtoInput.getLastName()));

        Trainee traineeToSave = traineeMapper.toEntity(traineeDtoInput);
        traineeToSave.setUser(user);

        Trainee trainee = traineeRepo.save(traineeToSave);

        if (traineeRegistrationCounter != null) {
            traineeRegistrationCounter.increment();
        }

        return traineeMapper.toSaveDto(trainee);
    }

    @Override
    @Transactional
    public TraineeDtoOutput getByUsername(String username, String password) {
        log.info("getByUsername, username = {}", username);

        User user = getUserByUsername(username);
        authenticate(password, user);

        Trainee trainee = traineeRepo.findByUserId(user.getId())
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));

        return traineeMapper.toDtoOutput(trainee);
    }

    @Override
    @Transactional
    public TraineeUpdateDtoOutput updateProfile(String username, String password,
                                                TraineeProfileDtoInput traineeDtoInput) {
        log.info("updateProfile, traineeDtoInput = {}", traineeDtoInput);

        User user = getUserByUsername(username);
        authenticate(password, user);

        Trainee trainee = traineeRepo.findByUserId(user.getId())
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));
        traineeMapper.updateTraineeProfile(trainee, traineeDtoInput);

        Trainee updatedTrainee = traineeRepo.save(trainee);

        return traineeMapper.toTraineeUpdateDto(updatedTrainee);
    }

    @Override
    @Transactional
    public TraineeUpdateListDtoOutput updateTrainerList(String username, String password, String traineeName,
                                                        List<TrainerShortDtoInput> trainersUsernames) {
        log.info("updateTrainerList, trainersUsernames = {}", trainersUsernames);

        User user = getUserByUsername(username);
        authenticate(password, user);

        List<Trainer> selectedTrainers = trainerRepo.findAllByUser_UsernameIn(
                trainersUsernames.stream().map(TrainerShortDtoInput::getUsername).toList());
        Trainee trainee = traineeRepo.findByUser_Username(traineeName)
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));
        trainee.setTrainers(selectedTrainers);

        Trainee updatedTrainee = traineeRepo.save(trainee);

        return traineeMapper.toTraineeUpdateListDtoOutput(updatedTrainee);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username, String password) {
        log.info("deleteByUsername, username = {}", username);

        User user = getUserByUsername(username);
        authenticate(password, user);

        traineeRepo.deleteById(user.getId());
    }

    private User getUserByUsername(String username) {
        return userService.findUserByUsername(username)
                          .orElseThrow(() -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));
    }

    public void authenticate(String password, User user) {
        if (authenticationService.checkAccess(password, user)) {
            throw new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE);
        }
    }
}
