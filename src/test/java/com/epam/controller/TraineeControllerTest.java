package com.epam.controller;

import com.epam.error.AccessException;
import com.epam.model.dto.TraineeDtoInput;
import com.epam.model.dto.TraineeDtoOutput;
import com.epam.model.dto.TraineeProfileDtoInput;
import com.epam.model.dto.TraineeSaveDtoOutput;
import com.epam.model.dto.TraineeUpdateDtoOutput;
import com.epam.model.dto.TraineeUpdateListDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerShortDtoInput;
import com.epam.model.dto.TrainingTypeShortOutputDto;
import com.epam.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @InjectMocks
    private TraineeController traineeController;

    @Mock
    private TraineeService traineeService;

    @Test
    void getByUsername_ShouldReturnTraineeDtoOutput() {
        String username = "testUser";
        String password = "testPassword";
        TraineeDtoOutput expectedOutput = createExpectedTraineeDtoOutput();

        when(traineeService.getByUsername(username, password)).thenReturn(expectedOutput);

        TraineeDtoOutput result = traineeController.getProfile(username, password);

        assertNotNull(result);
        assertEquals(expectedOutput, result);
    }

    @Test
    void save_ShouldReturnTraineeDtoOutput() {
        TraineeDtoInput traineeDtoInput = createTestTraineeDtoInput();
        TraineeSaveDtoOutput expectedOutput = createTraineeSaveDtoOutput();

        when(traineeService.save(traineeDtoInput)).thenReturn(expectedOutput);


        TraineeSaveDtoOutput result = traineeController.registration(traineeDtoInput);

        assertNotNull(result);
        assertEquals(expectedOutput, result);
    }


    @Test
    void updateProfile_ShouldReturnTraineeDtoOutput() {
        String username = "testUser";
        String password = "testPassword";
        TraineeProfileDtoInput traineeDtoInput = createTraineeProfileDtoInput();
        TraineeUpdateDtoOutput expectedOutput = createTraineeUpdateDtoOutput();

        when(traineeService.updateProfile(username, password, traineeDtoInput)).thenReturn(expectedOutput);

        TraineeUpdateDtoOutput result = traineeController.updateProfile(username, password, traineeDtoInput);

        assertNotNull(result);
        assertEquals(expectedOutput, result);
    }

    @Test
    void updateTrainerList_ShouldReturnTraineeDtoOutput() {
        String username = "testUser";
        String password = "testPassword";
        List<TrainerShortDtoInput> trainersUsernames = createTrainersUsernamesList();
        TraineeUpdateListDtoOutput expectedOutput = createTraineeUpdateListDtoOutput();

        when(traineeService.updateTrainerList(username, password, username, trainersUsernames)).thenReturn(expectedOutput);

        TraineeUpdateListDtoOutput result = traineeController.updateTrainerList(username, password, username, trainersUsernames);

        assertNotNull(result);
        assertEquals(expectedOutput, result);
    }

    @Test
    void deleteByUsername_ShouldReturnNoContentResponse() {
        String username = "testUser";
        String password = "testPassword";

        doNothing().when(traineeService).deleteByUsername(username, password);

        traineeController.deleteByUsername(username, password);

        verify(traineeService).deleteByUsername(username, password);
    }

    @Test
    void deleteByUsername_ShouldThrowAccessException() {
        String username = "testUser";
        String password = "testPassword";

        Mockito.doThrow(new AccessException("You don't have access for this."))
               .when(traineeService)
               .deleteByUsername(username, password);

        AccessException exception =
                assertThrows(AccessException.class, () -> traineeController.deleteByUsername(username, password));

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    public TraineeDtoInput createTestTraineeDtoInput() {
        return TraineeDtoInput.builder().dateOfBirth(LocalDate.of(1990, 5, 15)).address("123 Main Street").build();
    }

    public TraineeProfileDtoInput createTraineeProfileDtoInput() {
        return TraineeProfileDtoInput.builder()
                                     .firstName("Nikki")
                                     .lastName("Benz")
                                     .dateOfBirth(LocalDate.of(1974, 5, 7))
                                     .address("Mooning street 72")
                                     .isActive(true)
                                     .build();
    }

    public List<TrainerShortDtoInput> createTrainersUsernamesList() {
        List<TrainerShortDtoInput> trainers = new ArrayList<>();
        trainers.add(new TrainerShortDtoInput("john.smith"));
        trainers.add(new TrainerShortDtoInput("michel.past"));
        return trainers;
    }

    public TraineeDtoOutput createExpectedTraineeDtoOutput() {
        TraineeDtoOutput traineeDtoOutput = new TraineeDtoOutput();
        traineeDtoOutput.setDateOfBirth(LocalDate.of(1990, 5, 15));
        traineeDtoOutput.setAddress("123 Main Street");

        return traineeDtoOutput;
    }

    public TraineeUpdateListDtoOutput createTraineeUpdateListDtoOutput() {
        TrainerForTraineeDtoOutput trainer1 = TrainerForTraineeDtoOutput.builder()
                                                                        .firstName("Amir")
                                                                        .lastName("Ali")
                                                                        .username("amir.ali")
                                                                        .specialization(
                                                                                new TrainingTypeShortOutputDto("Gym"))
                                                                        .build();

        TrainerForTraineeDtoOutput trainer2 = TrainerForTraineeDtoOutput.builder()
                                                                        .firstName("Helga")
                                                                        .lastName("Parks")
                                                                        .username("helga.parks")
                                                                        .specialization(
                                                                                new TrainingTypeShortOutputDto("Yoga"))
                                                                        .build();

        return TraineeUpdateListDtoOutput.builder().trainers(List.of(trainer1, trainer2)).build();
    }

    public TraineeSaveDtoOutput createTraineeSaveDtoOutput() {
        return TraineeSaveDtoOutput.builder().username("john.doe").password("password").build();
    }

    public TraineeUpdateDtoOutput createTraineeUpdateDtoOutput() {
        return TraineeUpdateDtoOutput.builder()
                                     .username("john.doe")
                                     .lastName("Doe")
                                     .firstName("John")
                                     .dateOfBirth(LocalDate.of(1919, 5, 7))
                                     .address("Orange street 139")
                                     .isActive(true)
                                     .build();
    }
}