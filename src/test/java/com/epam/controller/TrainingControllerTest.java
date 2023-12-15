package com.epam.controller;


import com.epam.error.AccessException;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.service.TrainingService;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TrainingController trainingController;

    @Mock
    private TrainingService trainingService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
    }

    @Test
    void findByDateRangeAndTrainee_ShouldReturnListOfTrainingDtoOutput() throws Exception {
        List<TrainingForTraineeDtoOutput> expectedList = createTrainingForTraineeDtoOutputList();

        when(trainingService.findByDateRangeAndTraineeUsername(any(TrainingTraineeSpecification.class))).thenReturn(
                expectedList);

        mockMvc.perform(MockMvcRequestBuilders.get("/training/criteria-trainee")
                                              .param("periodFrom", "2024-01-01")
                                              .param("periodTo", "2024-12-31")
                                              .param("username", "testUser")
                                              .param("trainerName", "testTrainer")
                                              .param("trainingType", "Yoga")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(expectedList.get(0).getName()))
               .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(expectedList.get(1).getName()));
    }

    @Test
    void findByDateRangeAndTrainer_ShouldReturnListOfTrainingDtoOutput() throws Exception {
        List<TrainingForTrainerDtoOutput> expectedList = createTrainingForTrainerDtoOutputList();

        when(trainingService.findByDateRangeAndTrainerUsername(any(TrainingTrainerSpecification.class))).thenReturn(
                expectedList);

        mockMvc.perform(MockMvcRequestBuilders.get("/training/criteria-trainer")
                                              .param("periodFrom", "2024-01-01")
                                              .param("periodTo", "2024-12-31")
                                              .param("username", "testUser")
                                              .param("traineeName", "testTrainer")
                                              .param("trainingType", "Yoga")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(expectedList.get(0).getName()))
               .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(expectedList.get(1).getName()));
    }

    @Test
    void save_ShouldOk() {
        String username = "maria.dallas";
        String password = "password";
        TrainingDtoInput trainingDtoInput = createTestTrainingDtoInput();

        trainingController.save(username, password, trainingDtoInput);

        verify(trainingService).save(username, password, trainingDtoInput);
    }

    @Test
    void save_ShouldThrowAccessException() {
        String username = "maria.dallas";
        String password = "password";
        TrainingDtoInput trainingDtoInput = createTestTrainingDtoInput();

        Mockito.doThrow(new AccessException("You don't have access for this."))
               .when(trainingService)
               .save(username, password, trainingDtoInput);

        AccessException exception = assertThrows(AccessException.class,
                () -> trainingController.save(username, password, trainingDtoInput));

        assertEquals("You don't have access for this.", exception.getMessage());
    }

    private List<TrainingForTraineeDtoOutput> createTrainingForTraineeDtoOutputList() {
        TrainingForTraineeDtoOutput training1 = TrainingForTraineeDtoOutput.builder()
                                                                           .name("Training 1")
                                                                           .date(LocalDate.of(2024, 4, 5))
                                                                           .type("Yoga")
                                                                           .duration(60L)
                                                                           .trainerName("john.crow")
                                                                           .build();

        TrainingForTraineeDtoOutput training2 = TrainingForTraineeDtoOutput.builder()
                                                                           .name("Training 2")
                                                                           .date(LocalDate.of(2024, 2, 2))
                                                                           .type("Zumba")
                                                                           .duration(45L)
                                                                           .trainerName("jack.cat")
                                                                           .build();

        return List.of(training1, training2);
    }

    private List<TrainingForTrainerDtoOutput> createTrainingForTrainerDtoOutputList() {
        TrainingForTrainerDtoOutput training1 = TrainingForTrainerDtoOutput.builder()
                                                                           .name("Training 1")
                                                                           .date(LocalDate.of(2024, 4, 5))
                                                                           .type("Yoga")
                                                                           .duration(60L)
                                                                           .traineeName("john.crow")
                                                                           .build();

        TrainingForTrainerDtoOutput training2 = TrainingForTrainerDtoOutput.builder()
                                                                           .name("Training 2")
                                                                           .date(LocalDate.of(2024, 2, 2))
                                                                           .type("Zumba")
                                                                           .duration(45L)
                                                                           .traineeName("jack.cat")
                                                                           .build();

        return List.of(training1, training2);
    }

    private TrainingDtoInput createTestTrainingDtoInput() {
        return TrainingDtoInput.builder()
                               .traineeUsername("ronald.duck")
                               .trainerUsername("donald.ruck")
                               .name("Training 1")
                               .date(LocalDate.of(2023, 1, 1))
                               .duration(60L)
                               .build();
    }
}

