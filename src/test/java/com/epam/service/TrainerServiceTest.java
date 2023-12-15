package com.epam.service;

import com.epam.error.AccessException;
import com.epam.mapper.TrainerMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import com.epam.model.User;
import com.epam.model.dto.TraineeForTrainerDtoOutput;
import com.epam.model.dto.TrainerDtoInput;
import com.epam.model.dto.TrainerDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerProfileDtoInput;
import com.epam.model.dto.TrainerSaveDtoOutput;
import com.epam.model.dto.TrainerUpdateDtoOutput;
import com.epam.model.dto.TrainingTypeShortOutputDto;
import com.epam.model.dto.UserDtoInput;
import com.epam.repo.TrainerRepo;
import com.epam.repo.TrainingTypeRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerRepo trainerRepo;
    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private MeterRegistry meterRegistry;

    private User user;
    private List<Trainee> selectedTrainees;
    private TrainerDtoInput trainerDtoInput;
    private Trainer trainerToSave;
    private TrainerDtoOutput savedTrainer;
    private UserDtoInput userDtoInput;
    private TrainerProfileDtoInput trainerProfileDtoInput;
    private TrainerSaveDtoOutput trainerSaveDtoOutput;
    private TrainerUpdateDtoOutput trainerUpdateDtoOutput;

    @BeforeEach
    void setUp() {
        userDtoInput = createUserDtoInput();
        user = createUser(userDtoInput);
        selectedTrainees = createSelectedTrainees();
        trainerDtoInput = createTrainerDtoInput(user);
        trainerToSave = createTrainerToSave(trainerDtoInput, selectedTrainees, user);
        savedTrainer = createSavedTrainer(trainerToSave);
        trainerProfileDtoInput = createTrainerProfileDtoInput(user);
        trainerSaveDtoOutput = createTrainerSaveDtoOutput(trainerToSave);
        trainerUpdateDtoOutput = createTrainerUpdateDtoOutput(trainerToSave);
    }

    @Test
    void save_shouldReturnSavedTrainerDtoOutput() {
        when(trainerRepo.save(trainerToSave)).thenReturn(trainerToSave);
        when(trainerMapper.toEntity(trainerDtoInput)).thenReturn(trainerToSave);
        when(trainerMapper.toSaveDto(trainerToSave)).thenReturn(trainerSaveDtoOutput);
        when(trainingTypeRepo.findById(trainerDtoInput.getSpecialization())).thenReturn(
                Optional.ofNullable(trainerToSave.getTrainingType()));
        when(userService.save(userDtoInput)).thenReturn(user);

        TrainerSaveDtoOutput result = trainerService.save(trainerDtoInput);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
    }

    @Test
    void getByUsername_shouldOk() {
        when(trainerMapper.toDtoOutput(trainerToSave)).thenReturn(savedTrainer);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(trainerRepo.findByUserId(user.getId())).thenReturn(Optional.ofNullable(trainerToSave));

        TrainerDtoOutput result = trainerService.getByUsername(user.getUsername(), user.getPassword());

        assertNotNull(result);
        assertEquals(savedTrainer.getSpecialization(), result.getSpecialization());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    void updateProfile_WithValidInput_ShouldOk() {
        TrainerDtoInput updatedTrainer = createUpdatedTrainerDtoInput(user);
        Trainer updatedSavedTrainer = createTrainerToSave(updatedTrainer, selectedTrainees, user);

        when(trainerRepo.findByUserId(user.getId())).thenReturn(Optional.of(trainerToSave));
        when(trainerRepo.save(any(Trainer.class))).thenReturn(updatedSavedTrainer);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(trainerMapper.toTrainerUpdateDtoOutput(updatedSavedTrainer)).thenReturn(trainerUpdateDtoOutput);

        TrainerUpdateDtoOutput result =
                trainerService.updateProfile(user.getUsername(), user.getPassword(), trainerProfileDtoInput);

        assertNotNull(result);
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getIsActive(), result.getIsActive());
    }

    @Test
    void updateProfile_WithValidInput_IdDifferentIds() {
        TrainerDtoInput updatedTrainer = createUpdatedTrainerDtoInput(user);
        updatedTrainer.setSpecialization(trainerDtoInput.getSpecialization());
        Trainer updatedSavedTrainer = createTrainerToSave(updatedTrainer, selectedTrainees, user);

        when(trainerRepo.findByUserId(user.getId())).thenReturn(Optional.of(trainerToSave));
        when(trainerRepo.save(any(Trainer.class))).thenReturn(updatedSavedTrainer);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(trainerMapper.toTrainerUpdateDtoOutput(updatedSavedTrainer)).thenReturn(trainerUpdateDtoOutput);


        TrainerUpdateDtoOutput result =
                trainerService.updateProfile(user.getUsername(), user.getPassword(), trainerProfileDtoInput);

        assertNotNull(result);
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    void updateProfile_WithValidInput_ShouldThrowAccessException() {
        String username = user.getUsername();
        String password = user.getPassword();
        trainerProfileDtoInput.setSpecialization(99L);

        when(trainerRepo.findByUserId(user.getId())).thenReturn(Optional.of(trainerToSave));
        when(userService.findUserByUsername(username)).thenReturn(Optional.of(user));

        AccessException exception = assertThrows(AccessException.class,
                () -> trainerService.updateProfile(username, password, trainerProfileDtoInput),
                "An AccessException should be thrown when the user does not exist");

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void getTrainersWithEmptyTrainees_ShouldReturnEmptyList() {
        String username = user.getUsername();
        String password = user.getPassword();

        when(trainerRepo.findByTraineesIsEmptyAndUserIsActiveTrue()).thenReturn(new ArrayList<>());
        when(userService.findUserByUsername(username)).thenReturn(Optional.of(user));

        List<TrainerForTraineeDtoOutput> result = trainerService.getTrainersWithEmptyTrainees(username, password);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainersWithEmptyTrainees_ShouldReturnTrainersWithUserDetails() {
        String username = user.getUsername();
        String password = user.getPassword();
        List<Trainer> trainers = createTestTrainers();

        when(trainerRepo.findByTraineesIsEmptyAndUserIsActiveTrue()).thenReturn(trainers);
        when(userService.findUserByUsername(username)).thenReturn(Optional.ofNullable(user));

        List<TrainerForTraineeDtoOutput> result = trainerService.getTrainersWithEmptyTrainees(username, password);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(trainerRepo).findByTraineesIsEmptyAndUserIsActiveTrue();
    }

    @Test
    void authenticate_ValidPasswordAndMatchingIds_NoExceptionThrown() {
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);

        assertDoesNotThrow(() -> trainerService.authenticate(user.getPassword(), user),
                "Exception should not be thrown for valid credentials and matching IDs");

        verify(authenticationService).checkAccess(user.getPassword(), user);
    }

    @Test
    void authenticate_InvalidPassword_AccessExceptionThrown() {
        when(authenticationService.checkAccess("Invalid password", user)).thenReturn(true);

        AccessException exception =
                assertThrows(AccessException.class, () -> trainerService.authenticate("Invalid password", user),
                        "An AccessException should be thrown for invalid password");

        verify(authenticationService).checkAccess("Invalid password", user);

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void authenticate_MismatchedIds_AccessExceptionThrown() {
        String password = user.getPassword();
        user.setId(user.getId() + 1);

        when(authenticationService.checkAccess(password, user)).thenReturn(true);

        AccessException exception =
                assertThrows(AccessException.class, () -> trainerService.authenticate(password, user),
                        "An AccessException should be thrown for mismatched IDs");

        verify(authenticationService).checkAccess(user.getPassword(), user);

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void authenticate_ValidPassword_NoExceptionThrown() {
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);

        assertDoesNotThrow(() -> trainerService.authenticate(user.getPassword(), user),
                "No exception should be thrown for a valid password");

        verify(authenticationService).checkAccess(user.getPassword(), user);
    }

    @Test
    void authenticateWithTwoArguments_InvalidPassword_AccessExceptionThrown() {
        when(authenticationService.checkAccess("Invalid password", user)).thenReturn(true);

        AccessException exception =
                assertThrows(AccessException.class, () -> trainerService.authenticate("Invalid password", user),
                        "An AccessException should be thrown for an invalid password");

        verify(authenticationService).checkAccess("Invalid password", user);

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    public TrainerDtoInput createTrainerDtoInput(User user) {
        return TrainerDtoInput.builder()
                              .specialization(1L)
                              .firstName(user.getFirstName())
                              .lastName(user.getLastName())
                              .build();
    }

    public TrainerDtoInput createUpdatedTrainerDtoInput(User user) {
        return TrainerDtoInput.builder()
                              .specialization(2L)
                              .firstName(user.getFirstName())
                              .lastName(user.getLastName())
                              .build();
    }

    public User createUser(UserDtoInput userDtoInput) {
        return User.builder()
                   .id(1L)
                   .username(userDtoInput.getFirstName() + "." + userDtoInput.getLastName())
                   .firstName(userDtoInput.getFirstName())
                   .lastName(userDtoInput.getLastName())
                   .password("testPassword")
                   .isActive(true)
                   .postfix(0)
                   .build();
    }

    public UserDtoInput createUserDtoInput() {
        return UserDtoInput.builder().firstName("John").lastName("Doe").build();
    }

    public List<Trainee> createSelectedTrainees() {
        Trainee trainee1 = Trainee.builder()
                                  .id(2L)
                                  .dateOfBirth(LocalDate.of(1980, 4, 5))
                                  .address("Lockheed street 56")
                                  .user(User.builder()
                                            .lastName("Jane")
                                            .firstName("Collins")
                                            .username("jane.collins")
                                            .build())
                                  .trainers(new ArrayList<>(
                                          List.of(Trainer.builder().id(3L).trainees(new ArrayList<>()).build())))
                                  .build();

        Trainee trainee2 = Trainee.builder()
                                  .id(3L)
                                  .dateOfBirth(LocalDate.of(1980, 4, 5))
                                  .address("Lockdown street 17")
                                  .user(User.builder()
                                            .lastName("James")
                                            .firstName("Franko")
                                            .username("james.franko")
                                            .build())
                                  .trainers(new ArrayList<>(
                                          List.of(Trainer.builder().id(3L).trainees(new ArrayList<>()).build())))
                                  .build();

        return List.of(trainee1, trainee2);
    }

    public Trainer createTrainerToSave(TrainerDtoInput trainerDtoInput, List<Trainee> trainees, User user) {
        return Trainer.builder()
                      .trainingType(new TrainingType(trainerDtoInput.getSpecialization(), "Gym"))
                      .trainees(trainees)
                      .user(user)
                      .build();
    }

    public TrainerDtoOutput createSavedTrainer(Trainer trainer) {
        return TrainerDtoOutput.builder()
                               .specialization(new TrainingTypeShortOutputDto(trainer.getTrainingType().getName()))
                               .trainees(trainer.getTrainees()
                                                .stream()
                                                .map(this::toTraineeForTrainerDtoOutput)
                                                .collect(Collectors.toList()))
                               .firstName(trainer.getUser().getFirstName())
                               .lastName(trainer.getUser().getLastName())
                               .build();
    }

    private List<Trainer> createTestTrainers() {
        Trainer trainer1 = Trainer.builder()
                                  .id(1L)
                                  .trainingType(new TrainingType(1L, "Yoga"))
                                  .user(new User(1L, "Antonio", "Lopes", "antonio.lopes", "password1", true, 0))
                                  .trainees(new ArrayList<>())
                                  .build();


        Trainer trainer2 = Trainer.builder()
                                  .id(2L)
                                  .trainingType(new TrainingType(2L, "Box"))
                                  .user(new User(2L, "Hugo", "Boss", "hugo.boss", "password2", true, 0))
                                  .trainees(new ArrayList<>())
                                  .build();

        return List.of(trainer1, trainer2);
    }

    public TrainerProfileDtoInput createTrainerProfileDtoInput(User user) {
        return TrainerProfileDtoInput.builder()
                                     .firstName(user.getFirstName())
                                     .lastName(user.getLastName())
                                     .isActive(user.getIsActive())
                                     .specialization(1L)
                                     .build();
    }

    public TraineeForTrainerDtoOutput toTraineeForTrainerDtoOutput(Trainee trainee) {
        return TraineeForTrainerDtoOutput.builder()
                                         .username(trainee.getUser().getUsername())
                                         .firstName(trainee.getUser().getFirstName())
                                         .lastName(trainee.getUser().getLastName())
                                         .build();
    }

    public TrainerSaveDtoOutput createTrainerSaveDtoOutput(Trainer trainerToSave) {
        return TrainerSaveDtoOutput.builder()
                                   .username(trainerToSave.getUser().getUsername())
                                   .password(trainerToSave.getUser().getPassword())
                                   .build();
    }

    private TrainerUpdateDtoOutput createTrainerUpdateDtoOutput(Trainer trainer) {
        return TrainerUpdateDtoOutput.builder()
                                     .username(trainer.getUser().getUsername())
                                     .firstName(trainer.getUser().getFirstName())
                                     .lastName(trainer.getUser().getLastName())
                                     .specialization(
                                             new TrainingTypeShortOutputDto(trainer.getTrainingType().getName()))
                                     .isActive(trainer.getUser().getIsActive())
                                     .trainees(trainer.getTrainees()
                                                      .stream()
                                                      .map(this::toTraineeForTrainerDtoOutput)
                                                      .toList())
                                     .build();
    }
}
