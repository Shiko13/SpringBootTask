package com.epam.mapper;

import com.epam.model.Trainer;
import com.epam.model.User;
import com.epam.model.dto.TrainerDtoInput;
import com.epam.model.dto.TrainerDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerProfileDtoInput;
import com.epam.model.dto.TrainerSaveDtoOutput;
import com.epam.model.dto.TrainerUpdateDtoOutput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TraineeMapper.class, TrainingTypeMapper.class})
public interface TrainerMapper {

    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "username", expression = "java(appendPostfix(trainer.getUser()))")
    TrainerSaveDtoOutput toSaveDto(Trainer trainer);

    Trainer toEntity(TrainerDtoInput trainerDtoInput);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specialization", source = "trainingType")
    TrainerDtoOutput toDtoOutput(Trainer trainer);

    @Mapping(target = "username", expression = "java(appendPostfix(trainer.getUser()))")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "trainingType")
    TrainerForTraineeDtoOutput toTrainerForTraineeDtoOutput(Trainer trainer);

    @Mapping(target = "username", expression = "java(appendPostfix(trainer.getUser()))")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specialization", source = "trainingType")
    TrainerUpdateDtoOutput toTrainerUpdateDtoOutput(Trainer trainer);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    void updateTrainerProfile(@MappingTarget Trainer existingTrainer, TrainerProfileDtoInput trainerProfileDtoInput);

    default String appendPostfix(User user) {
        return (user.getPostfix() != 0) ? user.getUsername() + "-" + user.getPostfix() : user.getUsername();
    }
}
