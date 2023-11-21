package com.epam.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateDtoOutput {

    private String username;

    private String firstName;

    private String lastName;

    private TrainingTypeShortOutputDto specialization;

    @JsonProperty("isActive")
    private Boolean isActive;

    private List<TraineeForTrainerDtoOutput> trainees;
}
