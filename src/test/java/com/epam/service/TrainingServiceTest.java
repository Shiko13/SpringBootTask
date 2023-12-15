package com.epam.service;

import com.epam.error.AccessException;
import com.epam.mapper.TrainingMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import com.epam.model.User;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.repo.TraineeRepo;
import com.epam.repo.TrainerRepo;
import com.epam.repo.TrainingRepo;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;
import com.epam.spec.filter.TrainingTraineeFilter;
import com.epam.spec.filter.TrainingTrainerFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingRepo trainingRepo;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TraineeRepo traineeRepo;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @Test
    void save_shouldReturnSavedTrainingDtoOutput() {
        User user = createUser();
        Trainee trainee = createTrainee();
        Trainer trainer = createTrainer();

        TrainingDtoInput trainingDtoInput = createTrainingDtoInput();
        Training savedTraining = createTraining(trainingDtoInput);

        when(trainingRepo.save(any(Training.class))).thenReturn(savedTraining);
        when(trainingMapper.toEntity(any())).thenReturn(savedTraining);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);
        when(traineeRepo.findByUser_Username(trainingDtoInput.getTraineeUsername())).thenReturn(
                Optional.ofNullable(trainee));
        when(trainerRepo.findByUser_Username(trainingDtoInput.getTrainerUsername())).thenReturn(
                Optional.ofNullable(trainer));

        Training result = trainingService.save(user.getUsername(), user.getPassword(), trainingDtoInput);

        assertEquals(savedTraining.getId(), result.getId());
    }

    @Test
    void save_invalidTrainee_shouldThrowAccessError() {
        User user = createUser();
        String username = user.getUsername();
        String password = user.getPassword();
        Trainee trainee = createTrainee();

        TrainingDtoInput trainingDtoInput = createTrainingDtoInput();
        Training savedTraining = createTraining(trainingDtoInput);

        when(trainingMapper.toEntity(any())).thenReturn(savedTraining);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);
        when(traineeRepo.findByUser_Username(trainingDtoInput.getTraineeUsername())).thenReturn(
                Optional.ofNullable(trainee));

        AccessException exception = assertThrows(AccessException.class,
                () -> trainingService.save(username, password, trainingDtoInput),
                "An AccessException should be thrown when the trainer does not exist");

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void save_invalidTrainer_shouldThrowAccessError() {
        User user = createUser();
        String username = user.getUsername();
        String password = user.getPassword();

        TrainingDtoInput trainingDtoInput = createTrainingDtoInput();
        Training savedTraining = createTraining(trainingDtoInput);

        when(trainingMapper.toEntity(any())).thenReturn(savedTraining);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);

        AccessException exception = assertThrows(AccessException.class,
                () -> trainingService.save(username, password, trainingDtoInput),
                "An AccessException should be thrown when the trainer does not exist");

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void findByDateRangeAndTraineeUsername_shouldReturnTrainings() {
        TrainingTraineeFilter filter = new TrainingTraineeFilter();
        TrainingTraineeSpecification specification = new TrainingTraineeSpecification(filter);

        List<Training> testTrainings = createTestTrainings();

        when(trainingRepo.findAll(any(TrainingTraineeSpecification.class))).thenReturn(testTrainings);
        when(trainingMapper.toTrainingForTraineeDtoList(testTrainings)).thenReturn(toDtoList(testTrainings));

        List<TrainingForTraineeDtoOutput> result = trainingService.findByDateRangeAndTraineeUsername(specification);

        assertEquals(testTrainings.size(), result.size());
    }

    @Test
    void findByDateRangeAndTrainerUsername_shouldReturnTrainings() {
        TrainingTrainerFilter filter = new TrainingTrainerFilter();
        TrainingTrainerSpecification specification = new TrainingTrainerSpecification(filter);

        List<Training> testTrainings = createTestTrainings();

        when(trainingRepo.findAll(any(TrainingTrainerSpecification.class))).thenReturn(testTrainings);
        when(trainingMapper.toTrainingForTrainerDtoList(testTrainings)).thenReturn(toDtoTrainerList(testTrainings));

        List<TrainingForTrainerDtoOutput> result = trainingService.findByDateRangeAndTrainerUsername(specification);

        assertEquals(testTrainings.size(), result.size());
    }

    @Test
    void authenticate_InvalidPassword_AccessExceptionThrown() {
        User user = createUser();

        when(authenticationService.checkAccess("Invalid password", user)).thenReturn(true);

        AccessException exception =
                assertThrows(AccessException.class, () -> trainingService.authenticate("Invalid password", user),
                        "An AccessException should be thrown for invalid password");

        verify(authenticationService).checkAccess("Invalid password", user);

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    public TrainingDtoInput createTrainingDtoInput() {
        return TrainingDtoInput.builder()
                               .name("Test Training")
                               .trainerUsername("Trainer Username")
                               .traineeUsername("Trainee Username")
                               .date(LocalDate.of(2023, 1, 15))
                               .duration(120L)
                               .build();

    }

    public Training createTraining(TrainingDtoInput trainingDtoInput) {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        TrainingType trainingType = new TrainingType(1L, "Yoga");

        return Training.builder()
                       .id(1L)
                       .trainee(Trainee.builder()
                                       .id(1L)
                                       .dateOfBirth(LocalDate.of(2001, 4, 5))
                                       .address("Baker street 50")
                                       .user(user1)
                                       .trainers(new ArrayList<>())
                                       .build())
                       .trainer(new Trainer(2L, trainingType, user2, new ArrayList<>()))
                       .name(trainingDtoInput.getName())
                       .trainingType(trainingType)
                       .date(trainingDtoInput.getDate())
                       .duration(trainingDtoInput.getDuration())
                       .build();
    }

    public List<Training> createTestTrainings() {
        List<Training> trainings = new ArrayList<>();

        Training training1 = new Training();
        training1.setId(1L);
        trainings.add(training1);

        Training training2 = new Training();
        training2.setId(2L);
        trainings.add(training2);

        return trainings;
    }

    private List<TrainingForTraineeDtoOutput> toDtoList(List<Training> trainings) {
        if (trainings == null) {
            return null;
        }

        List<TrainingForTraineeDtoOutput> list = new ArrayList<>(trainings.size());
        for (Training training : trainings) {
            list.add(toDtoOutput(training));
        }

        return list;
    }

    private List<TrainingForTrainerDtoOutput> toDtoTrainerList(List<Training> trainings) {
        if (trainings == null) {
            return null;
        }

        List<TrainingForTrainerDtoOutput> list = new ArrayList<>(trainings.size());
        for (Training training : trainings) {
            list.add(toTrainerDtoOutput(training));
        }

        return list;
    }

    private TrainingForTraineeDtoOutput toDtoOutput(Training training) {
        return TrainingForTraineeDtoOutput.builder()
                                          .name(training.getName())
                                          .date(training.getDate())
                                          .duration(training.getDuration())
                                          .build();
    }

    private TrainingForTrainerDtoOutput toTrainerDtoOutput(Training training) {
        return TrainingForTrainerDtoOutput.builder()
                                          .name(training.getName())
                                          .date(training.getDate())
                                          .duration(training.getDuration())
                                          .build();
    }

    public User createUser() {
        return User.builder()
                   .id(1L)
                   .lastName("Harry")
                   .firstName("Jones")
                   .username("harry.jones")
                   .password("password")
                   .postfix(0)
                   .isActive(true)
                   .build();
    }

    public Trainee createTrainee() {
        return Trainee.builder()
                      .id(1L)
                      .dateOfBirth(LocalDate.of(2001, 5, 5))
                      .address("Apple street 58")
                      .user(createUser())
                      .trainers(new ArrayList<>())
                      .build();
    }

    public Trainer createTrainer() {
        return Trainer.builder()
                      .id(2L)
                      .user(new User())
                      .trainees(new ArrayList<>())
                      .trainingType(new TrainingType(1L, "Yoga"))
                      .build();
    }
}