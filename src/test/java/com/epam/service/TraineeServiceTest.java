package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.NotFoundException;
import com.epam.mapper.TraineeMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import com.epam.model.User;
import com.epam.model.dto.TraineeDtoInput;
import com.epam.model.dto.TraineeDtoOutput;
import com.epam.model.dto.TraineeProfileDtoInput;
import com.epam.model.dto.TraineeSaveDtoOutput;
import com.epam.model.dto.TraineeUpdateDtoOutput;
import com.epam.model.dto.TraineeUpdateListDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerShortDtoInput;
import com.epam.model.dto.TrainingTypeShortOutputDto;
import com.epam.model.dto.UserDtoInput;
import com.epam.repo.TraineeRepo;
import com.epam.repo.TrainerRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeRepo traineeRepo;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MeterRegistry meterRegistry;
    private User user;
    private UserDtoInput userDtoInput;
    private List<Trainer> selectedTrainers;
    private TraineeDtoInput traineeDtoInput;
    private Trainee traineeToSave;
    private TraineeDtoOutput savedTrainee;
    private TraineeSaveDtoOutput saveDtoOutput;
    private TraineeUpdateDtoOutput traineeUpdateDtoOutput;

    private TraineeUpdateListDtoOutput traineeUpdateListDtoOutput;

    @BeforeEach
    void setUp() {
        userDtoInput = createUserDtoInput();
        user = createUser(userDtoInput);
        selectedTrainers = createSelectedTrainers();
        traineeDtoInput = createTraineeDtoInput(user);
        traineeToSave = createTraineeToSave(traineeDtoInput, selectedTrainers, user);
        savedTrainee = createSavedTrainee(traineeToSave);
        saveDtoOutput = createSavedTraineeSaveDto(traineeToSave);
        traineeUpdateDtoOutput = createTraineeUpdateDtoOutput(traineeToSave);
        traineeUpdateListDtoOutput = createTraineeUpdateListDtoOutput(traineeToSave);
    }

    @Test
    void save_shouldReturnSavedTraineeDtoOutput() {
        when(traineeRepo.save(traineeToSave)).thenReturn(traineeToSave);
        when(traineeMapper.toEntity(traineeDtoInput)).thenReturn(traineeToSave);
        when(traineeMapper.toSaveDto(traineeToSave)).thenReturn(saveDtoOutput);
        when(userService.save(userDtoInput)).thenReturn(user);

        TraineeSaveDtoOutput result = traineeService.save(traineeDtoInput);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
    }

    @Test
    void getByUsername_shouldOk() {
        when(traineeRepo.findByUserId(user.getId())).thenReturn(Optional.of(traineeToSave));
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(traineeMapper.toDtoOutput(traineeToSave)).thenReturn(savedTrainee);

        TraineeDtoOutput result = traineeService.getByUsername(user.getUsername(), user.getPassword());

        assertNotNull(result);
        assertEquals(savedTrainee.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(savedTrainee.getAddress(), result.getAddress());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(savedTrainee.getTrainers(), result.getTrainers());
    }

    @Test
    void getByUsername_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        String username = user.getUsername();
        String password = user.getPassword();

        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));

        assertThrows(NotFoundException.class, () -> traineeService.getByUsername(username, password),
                "A NotFoundException should be thrown.");
    }

    @Test
    void updateProfile_WithValidInput_ShouldReturnUpdatedTraineeDtoOutput() {
        TraineeProfileDtoInput updatedTrainee = createUpdatedProfileTraineeDtoInput(user);
        Trainee updatedSavedTrainee = createTraineeToUpdateProfile(updatedTrainee, selectedTrainers, user);
        traineeUpdateDtoOutput.setDateOfBirth(LocalDate.of(1987, 7, 7));
        traineeUpdateDtoOutput.setAddress("Api street 49");

        when(traineeRepo.findByUserId(user.getId())).thenReturn(Optional.of(traineeToSave));
        when(traineeRepo.save(any(Trainee.class))).thenReturn(updatedSavedTrainee);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(traineeMapper.toTraineeUpdateDto(updatedSavedTrainee)).thenReturn(traineeUpdateDtoOutput);

        TraineeUpdateDtoOutput result =
                traineeService.updateProfile(user.getUsername(), user.getPassword(), updatedTrainee);

        assertNotNull(result);
        assertEquals(updatedTrainee.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(updatedTrainee.getAddress(), result.getAddress());
//        assertEquals(updatedTrainee.getUser().getLastName(), result.getUser().getLastName());
    }

    @Test
    void deleteByUsername_WithValidInput_ShouldDeleteTraineeAndUser() {
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        traineeToSave.setId(user.getId());

        assertDoesNotThrow(() -> traineeService.deleteByUsername(user.getUsername(), user.getPassword()));

        verify(traineeRepo).deleteById(traineeToSave.getId());
    }

    @Test
    void updateTrainerList_WithValidInput_ShouldUpdateTrainerList() {
        List<Trainer> updatedTrainers = createUpdatedSelectedTrainers();
        List<TrainerShortDtoInput> trainersUsernames = toTrainersUsernames(traineeToSave);

        traineeToSave.setTrainers(updatedTrainers);

        when(traineeRepo.findByUser_Username(user.getUsername())).thenReturn(Optional.of(traineeToSave));
        when(traineeRepo.save(any(Trainee.class))).thenReturn(traineeToSave);
        when(userService.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(traineeMapper.toTraineeUpdateListDtoOutput(traineeToSave)).thenReturn(traineeUpdateListDtoOutput);

        TraineeUpdateListDtoOutput result =
                traineeService.updateTrainerList(user.getUsername(), user.getPassword(), user.getUsername(), trainersUsernames);

        assertNotNull(result);
//        assertEquals(updatedTrainerIds, savedTrainee.getTrainerIds());
    }

    @Test
    void authenticate_ValidPasswordAndMatchingIds_NoExceptionThrown() {
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);

        assertDoesNotThrow(() -> traineeService.authenticate(user.getPassword(), user),
                "Exception should not be thrown for valid credentials and matching IDs");

        verify(authenticationService).checkAccess(user.getPassword(), user);
    }

    @Test
    void authenticate_InvalidPassword_AccessExceptionThrown() {
        when(authenticationService.checkAccess("Invalid password", user)).thenReturn(true);

        AccessException exception =
                assertThrows(AccessException.class, () -> traineeService.authenticate("Invalid password", user),
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
                assertThrows(AccessException.class, () -> traineeService.authenticate(password, user),
                        "An AccessException should be thrown for mismatched IDs");

        verify(authenticationService).checkAccess(user.getPassword(), user);

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    @Test
    void authenticate_ValidPassword_NoExceptionThrown() {
        when(authenticationService.checkAccess(user.getPassword(), user)).thenReturn(false);

        assertDoesNotThrow(() -> traineeService.authenticate(user.getPassword(), user),
                "No exception should be thrown for a valid password");

        verify(authenticationService).checkAccess(user.getPassword(), user);
    }

    @Test
    void authenticate_InvalidPassword_AccessExceptionThrown2() {
        when(authenticationService.checkAccess("Invalid password", user)).thenReturn(true);

        AccessException exception =
                assertThrows(AccessException.class, () -> traineeService.authenticate("Invalid password", user),
                        "An AccessException should be thrown for an invalid password");

        verify(authenticationService).checkAccess("Invalid password", user);

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    public TraineeDtoInput createTraineeDtoInput(User user) {
        return TraineeDtoInput.builder()
                              .dateOfBirth(LocalDate.of(1985, 5, 7))
                              .address("Common street 53")
                              .firstName(user.getFirstName())
                              .lastName(user.getLastName())
                              .build();
    }

    public TraineeProfileDtoInput createUpdatedProfileTraineeDtoInput(User user) {
        return TraineeProfileDtoInput.builder()
                                     .dateOfBirth(LocalDate.of(1987, 7, 7))
                                     .address("Api street 49")
                                     .firstName(user.getFirstName())
                                     .lastName(user.getLastName())
                                     .isActive(user.getIsActive())
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

    public List<Trainer> createSelectedTrainers() {
        Trainer trainer1 = Trainer.builder()
                                  .id(2L)
                                  .user(User.builder()
                                            .firstName("Jack")
                                            .lastName("Ripper")
                                            .username("jack.ripper")
                                            .build())
                                  .trainingType(new TrainingType(1L, "Yoga"))
                                  .build();

        Trainer trainer2 = Trainer.builder()
                                  .id(3L)
                                  .user(User.builder()
                                            .firstName("Mike")
                                            .lastName("Stripper")
                                            .username("mike.stripper")
                                            .build())
                                  .trainingType(new TrainingType(2L, "Gym"))
                                  .build();

        return List.of(trainer1, trainer2);
    }

    public List<Trainer> createUpdatedSelectedTrainers() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(4L);

        Trainer trainer2 = new Trainer();
        trainer2.setId(5L);

        return List.of(trainer1, trainer2);
    }

    public Trainee createTraineeToSave(TraineeDtoInput traineeDtoInput, List<Trainer> trainers, User user) {
        return Trainee.builder()
                      .address(traineeDtoInput.getAddress())
                      .dateOfBirth(traineeDtoInput.getDateOfBirth())
                      .trainers(trainers)
                      .user(user)
                      .build();
    }

    public Trainee createTraineeToUpdateProfile(TraineeProfileDtoInput traineeDtoInput, List<Trainer> trainers,
                                                User user) {
        return Trainee.builder()
                      .address(traineeDtoInput.getAddress())
                      .dateOfBirth(traineeDtoInput.getDateOfBirth())
                      .trainers(trainers)
                      .user(user)
                      .build();
    }

    public TraineeDtoOutput createSavedTrainee(Trainee trainee) {
        return TraineeDtoOutput.builder()
                               .address(trainee.getAddress())
                               .dateOfBirth(trainee.getDateOfBirth())
                               .trainers(trainee.getTrainers()
                                                .stream()
                                                .map(this::toTrainerForTraineeDtoOutputMapper)
                                                .collect(Collectors.toList()))
                               .firstName(user.getFirstName())
                               .lastName(user.getLastName())
                               .build();
    }

    public TraineeSaveDtoOutput createSavedTraineeSaveDto(Trainee trainee) {
        return TraineeSaveDtoOutput.builder()
                                   .username(trainee.getUser().getUsername())
                                   .password(trainee.getUser().getPassword())
                                   .build();
    }

    public TrainerForTraineeDtoOutput toTrainerForTraineeDtoOutputMapper(Trainer trainer) {
        return TrainerForTraineeDtoOutput.builder()
                                         .firstName(trainer.getUser().getFirstName())
                                         .lastName(trainer.getUser().getLastName())
                                         .username(trainer.getUser().getUsername())
                                         .specialization(
                                                 new TrainingTypeShortOutputDto(trainer.getTrainingType().getName()))
                                         .build();
    }

    public TraineeUpdateDtoOutput createTraineeUpdateDtoOutput(Trainee trainee) {
        return TraineeUpdateDtoOutput.builder()
                                     .username(trainee.getUser().getUsername())
                                     .firstName(trainee.getUser().getFirstName())
                                     .lastName(trainee.getUser().getLastName())
                                     .dateOfBirth(trainee.getDateOfBirth())
                                     .address(trainee.getAddress())
                                     .isActive(trainee.getUser().getIsActive())
                                     .trainers(trainee.getTrainers()
                                                      .stream()
                                                      .map(this::toTrainerForTraineeDtoOutputMapper)
                                                      .collect(Collectors.toList()))
                                     .build();
    }

    public List<TrainerShortDtoInput> toTrainersUsernames(Trainee trainee) {
        return trainee.getTrainers().stream().map(this::toTrainerShortDtoInput).collect(Collectors.toList());
    }

    private TrainerShortDtoInput toTrainerShortDtoInput(Trainer trainer) {
        return new TrainerShortDtoInput(trainer.getUser().getUsername());
    }

    public TraineeUpdateListDtoOutput createTraineeUpdateListDtoOutput(Trainee trainee) {
        return new TraineeUpdateListDtoOutput(
                trainee.getTrainers().stream().map(this::toTrainerForTraineeDtoOutputMapper).toList());
    }

}