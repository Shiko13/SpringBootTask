package com.epam.service;

import com.epam.mapper.TrainingTypeMapper;
import com.epam.model.dto.TrainingTypeOutputDto;
import com.epam.repo.TrainingTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepo trainingTypeRepo;

    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public List<TrainingTypeOutputDto> getAll() {
        return trainingTypeMapper.toDtoList(trainingTypeRepo.findAll());
    }
}
