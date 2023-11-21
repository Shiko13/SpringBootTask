package com.epam.mapper;

import com.epam.model.TrainingType;
import com.epam.model.dto.TrainingTypeOutputDto;
import com.epam.model.dto.TrainingTypeShortOutputDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeShortOutputDto toShortDto(TrainingType trainingType);

    TrainingTypeOutputDto toDto(TrainingType trainingType);

    List<TrainingTypeOutputDto> toDtoList(List<TrainingType> trainingTypes);
}
