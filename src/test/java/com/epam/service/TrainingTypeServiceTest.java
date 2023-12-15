package com.epam.service;

import com.epam.mapper.TrainingTypeMapper;
import com.epam.model.TrainingType;
import com.epam.model.dto.TrainingTypeOutputDto;
import com.epam.repo.TrainingTypeRepo;
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
class TrainingTypeServiceTest {

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @Test
    void getAll_ShouldReturn_ListOfTrainingTypeOutputDto() {
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        trainingType1.setName("Type 1");

        TrainingType trainingType2 = new TrainingType();
        trainingType2.setId(2L);
        trainingType2.setName("Type 2");

        List<TrainingType> trainingTypes = Arrays.asList(trainingType1, trainingType2);

        TrainingTypeOutputDto outputDto1 = new TrainingTypeOutputDto(1L, "Type 1");

        TrainingTypeOutputDto outputDto2 = new TrainingTypeOutputDto(2L, "Type 2");

        List<TrainingTypeOutputDto> outputDtoList = Arrays.asList(outputDto1, outputDto2);

        when(trainingTypeRepo.findAll()).thenReturn(trainingTypes);
        when(trainingTypeMapper.toDtoList(trainingTypes)).thenReturn(outputDtoList);

        List<TrainingTypeOutputDto> result = trainingTypeService.getAll();

        assertEquals(outputDtoList, result);

        verify(trainingTypeRepo).findAll();
        verify(trainingTypeMapper).toDtoList(trainingTypes);
    }
}
