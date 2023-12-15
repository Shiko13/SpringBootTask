package com.epam.controller;

import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.service.TrainingService;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;
import com.epam.spec.filter.TrainingTraineeFilter;
import com.epam.spec.filter.TrainingTrainerFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
@Api(tags = "Training Controller")
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping("/criteria-trainee")
    @ApiOperation(value = "Find trainings of trainee by date range and trainer's name")
    public List<TrainingForTraineeDtoOutput> findByDateRangeAndTrainee(TrainingTraineeFilter filter) {
        return trainingService.findByDateRangeAndTraineeUsername(new TrainingTraineeSpecification(filter));
    }

    @GetMapping("/criteria-trainer")
    @ApiOperation(value = "Find trainings of trainer by date range and trainee's name")
    public List<TrainingForTrainerDtoOutput> findByDateRangeAndTrainer(TrainingTrainerFilter filter) {
        return trainingService.findByDateRangeAndTrainerUsername(new TrainingTrainerSpecification(filter));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Save Training", notes = "Create a new training based on the provided input.")
    public void save(@RequestParam String username, @RequestParam String password,
                     @RequestBody TrainingDtoInput trainingDtoInput) {
        trainingService.save(username, password, trainingDtoInput);
    }
}
