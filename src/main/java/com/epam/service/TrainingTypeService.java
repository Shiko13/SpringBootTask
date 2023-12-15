package com.epam.service;

import com.epam.model.dto.TrainingTypeOutputDto;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeOutputDto> getAll();
}
