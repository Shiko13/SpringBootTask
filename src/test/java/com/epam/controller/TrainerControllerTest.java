package com.epam.controller;

import com.epam.model.dto.TrainerDtoInput;
import com.epam.model.dto.TrainerDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerProfileDtoInput;
import com.epam.model.dto.TrainerSaveDtoOutput;
import com.epam.model.dto.TrainerUpdateDtoOutput;
import com.epam.model.dto.TrainingTypeShortOutputDto;
import com.epam.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @InjectMocks
    private TrainerController trainerController;

    @Mock
    private TrainerService trainerService;

    @Test
    void getByUserName_ShouldReturnTrainerDtoOutput() {
        TrainerDtoOutput expected = createExpectedTrainerDtoOutput();
        when(trainerService.getByUsername("testUser", "testPassword")).thenReturn(expected);

        TrainerDtoOutput result = trainerController.getProfile("testUser", "testPassword");

        assertNotNull(result);
        assertEquals(expected.getFirstName(), result.getFirstName());
        assertEquals(expected.getLastName(), result.getLastName());
        assertEquals(expected.getSpecialization(), result.getSpecialization());
    }

    @Test
    void getTrainersWithEmptyTrainees_ShouldReturnListOfTrainerDtoOutput() {
        String username = "john.doe";
        String password = "password";

        List<TrainerForTraineeDtoOutput> expectedList = toTrainerForTraineeDtoOutputList();
        when(trainerService.getTrainersWithEmptyTrainees(username, password)).thenReturn(expectedList);

        List<TrainerForTraineeDtoOutput> resultList =
                trainerController.getTrainersWithEmptyTrainees(username, password);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertThat(resultList, is(expectedList));
    }

    @Test
    void save_ShouldReturnTrainerDtoOutput() {
        TrainerDtoInput trainerDtoInput = createTestTrainerDtoInput();
        TrainerSaveDtoOutput expected = toTrainerSaveDtoOutput();


        when(trainerService.save(trainerDtoInput)).thenReturn(expected);


        TrainerSaveDtoOutput result = trainerController.registration(trainerDtoInput);

        assertNotNull(result);
        assertEquals(expected.getUsername(), result.getUsername());
        assertEquals(expected.getPassword(), result.getPassword());
    }


    @Test
    void updateProfile_ShouldReturnTrainerDtoOutput() {
        TrainerProfileDtoInput trainerDtoInput = createTrainerProfileDtoInput();
        TrainerUpdateDtoOutput expected = createExpectedTrainerUpdateDtoOutput();

        when(trainerService.updateProfile("testUser", "testPassword", trainerDtoInput)).thenReturn(expected);

        TrainerUpdateDtoOutput result = trainerController.updateProfile("testUser", "testPassword", trainerDtoInput);

        assertNotNull(result);
        assertEquals(expected.getUsername(), result.getUsername());
        assertEquals(expected.getLastName(), result.getLastName());
        assertEquals(expected.getSpecialization(), result.getSpecialization());
    }

    private TrainerDtoOutput createExpectedTrainerDtoOutput() {
        return TrainerDtoOutput.builder()
                               .firstName("Uri")
                               .lastName("Geller")
                               .specialization(new TrainingTypeShortOutputDto("Box"))
                               .isActive(true)
                               .build();
    }

    private TrainerUpdateDtoOutput createExpectedTrainerUpdateDtoOutput() {
        return TrainerUpdateDtoOutput.builder()
                                     .firstName("Uri")
                                     .lastName("Geller")
                                     .username("uri.geller")
                                     .specialization(new TrainingTypeShortOutputDto("Box"))
                                     .isActive(true)
                                     .build();
    }

    private List<TrainerForTraineeDtoOutput> toTrainerForTraineeDtoOutputList() {
        List<TrainerForTraineeDtoOutput> trainers = new ArrayList<>();

        TrainerForTraineeDtoOutput trainer1 = TrainerForTraineeDtoOutput.builder()
                                                                        .username("john.doe")
                                                                        .firstName("John")
                                                                        .lastName("Doe")
                                                                        .specialization(
                                                                                new TrainingTypeShortOutputDto("Gym"))
                                                                        .build();

        TrainerForTraineeDtoOutput trainer2 = TrainerForTraineeDtoOutput.builder()
                                                                        .username("jack.nicholson")
                                                                        .firstName("Jack")
                                                                        .lastName("Nicholson")
                                                                        .specialization(
                                                                                new TrainingTypeShortOutputDto("Yoga"))
                                                                        .build();

        trainers.add(trainer1);
        trainers.add(trainer2);
        return trainers;
    }


    private TrainerDtoInput createTestTrainerDtoInput() {
        return TrainerDtoInput.builder().firstName("Monica").lastName("Chandler").specialization(1L).build();
    }

    private TrainerSaveDtoOutput toTrainerSaveDtoOutput() {
        return TrainerSaveDtoOutput.builder().username("andre.ramos").password("password").build();
    }

    private TrainerProfileDtoInput createTrainerProfileDtoInput() {
        return TrainerProfileDtoInput.builder()
                                     .firstName("Piter")
                                     .lastName("Hudson")
                                     .isActive(true)
                                     .specialization(1L)
                                     .build();
    }
}
