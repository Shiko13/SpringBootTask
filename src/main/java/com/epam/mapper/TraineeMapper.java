package com.epam.mapper;

import com.epam.model.Trainee;
import com.epam.model.User;
import com.epam.model.dto.TraineeDtoInput;
import com.epam.model.dto.TraineeDtoOutput;
import com.epam.model.dto.TraineeForTrainerDtoOutput;
import com.epam.model.dto.TraineeProfileDtoInput;
import com.epam.model.dto.TraineeSaveDtoOutput;
import com.epam.model.dto.TraineeUpdateDtoOutput;
import com.epam.model.dto.TraineeUpdateListDtoOutput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TrainerMapper.class})
public interface TraineeMapper {

    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "username", expression = "java(appendPostfix(trainee.getUser()))")
    TraineeSaveDtoOutput toSaveDto(Trainee trainee);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "username", expression = "java(appendPostfix(trainee.getUser()))")
    TraineeUpdateDtoOutput toTraineeUpdateDto(Trainee trainee);

    Trainee toEntity(TraineeDtoInput traineeDtoInput);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    TraineeDtoOutput toDtoOutput(Trainee trainee);

    TraineeUpdateListDtoOutput toTraineeUpdateListDtoOutput(Trainee trainee);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    void updateTraineeProfile(@MappingTarget Trainee existingTrainee, TraineeProfileDtoInput traineeDtoInput);

    @Mapping(target = "username", expression = "java(appendPostfix(trainee.getUser()))")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    TraineeForTrainerDtoOutput toTraineeForTrainerDtoOutput(Trainee trainee);
    default String appendPostfix(User user) {
        return (user.getPostfix() != 0) ? user.getUsername() + "-" + user.getPostfix() : user.getUsername();
    }
}
