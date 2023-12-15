package com.epam.controller;

import com.epam.model.dto.TrainingTypeOutputDto;
import com.epam.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @Test
    void getAll_ShouldReturn_ListOfTrainingForTraineeDtoOutput() {
        TrainingTypeOutputDto type1 = new TrainingTypeOutputDto(1L, "Type 1");
        TrainingTypeOutputDto type2 = new TrainingTypeOutputDto(2L, "Type 2");

        List<TrainingTypeOutputDto> expectedTypes = Arrays.asList(type1, type2);

        when(trainingTypeService.getAll()).thenReturn(expectedTypes);

        List<TrainingTypeOutputDto> actualTypes = trainingTypeController.getAll();

        assertEquals(expectedTypes, actualTypes);

        verify(trainingTypeService).getAll();
    }
}
