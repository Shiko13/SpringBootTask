package com.epam.controller;

import com.epam.model.dto.TrainerDtoInput;
import com.epam.model.dto.TrainerDtoOutput;
import com.epam.model.dto.TrainerForTraineeDtoOutput;
import com.epam.model.dto.TrainerProfileDtoInput;
import com.epam.model.dto.TrainerSaveDtoOutput;
import com.epam.model.dto.TrainerUpdateDtoOutput;
import com.epam.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/trainer")
@Api(tags = "Trainer Controller")
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/username")
    @ApiOperation("Get trainer by username")
    public TrainerDtoOutput getProfile(@RequestParam @Pattern(regexp = "[a-z]+\\.[\\w-]+",
                                                              message = "Invalid input format") String username, @RequestParam String password) {
        return trainerService.getByUsername(username, password);
    }

    @GetMapping("/free")
    @ApiOperation("Get not assigned on trainee active trainers")
    public List<TrainerForTraineeDtoOutput> getTrainersWithEmptyTrainees(@RequestParam String username,
                                                                         @RequestParam String password) {
        return trainerService.getTrainersWithEmptyTrainees(username, password);
    }

    @PostMapping()
    @ApiOperation("Trainer registration")
    public TrainerSaveDtoOutput registration(@RequestBody TrainerDtoInput trainerDtoInput) {
        return trainerService.save(trainerDtoInput);
    }

    @PutMapping("/profile")
    @ApiOperation("Update Trainer Profile")
    public TrainerUpdateDtoOutput updateProfile(@RequestParam String username, @RequestParam String password,
                                                @RequestBody TrainerProfileDtoInput trainerDtoInput) {
        return trainerService.updateProfile(username, password, trainerDtoInput);
    }
}



