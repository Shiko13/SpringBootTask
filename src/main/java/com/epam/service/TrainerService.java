package com.epam.service;

import com.epam.model.dto.TrainerDtoInput;
import com.epam.model.dto.TrainerDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerProfileDtoInput;
import com.epam.model.dto.TrainerSaveDtoOutput;
import com.epam.model.dto.TrainerUpdateDtoOutput;

import java.util.List;

public interface TrainerService {

    TrainerSaveDtoOutput save(TrainerDtoInput trainerDtoInput);

    TrainerDtoOutput getByUsername(String username, String password);

    TrainerUpdateDtoOutput updateProfile(String username, String password, TrainerProfileDtoInput trainerDtoInput);

    List<TrainerForTraineeDtoOutput> getTrainersWithEmptyTrainees(String username, String password);
}
