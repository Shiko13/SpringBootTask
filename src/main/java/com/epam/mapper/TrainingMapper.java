package com.epam.mapper;

import com.epam.model.Training;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {TrainerMapper.class, TraineeMapper.class, TrainingTypeMapper.class})
public interface TrainingMapper {

    Training toEntity(TrainingDtoInput trainingDtoInput);

    List<TrainingForTraineeDtoOutput> toTrainingForTraineeDtoList(List<Training> trainings);

    List<TrainingForTrainerDtoOutput> toTrainingForTrainerDtoList(List<Training> trainings);

    @Mapping(target = "type", source = "trainingType.name")
    @Mapping(target = "trainerName", source = "trainer.user.username")
    TrainingForTraineeDtoOutput toTrainingForTraineeDto(Training training);

    @Mapping(target = "type", source = "trainingType.name")
    @Mapping(target = "traineeName", source = "trainee.user.username")
    TrainingForTrainerDtoOutput toTrainingForTrainerDto(Training training);
}
