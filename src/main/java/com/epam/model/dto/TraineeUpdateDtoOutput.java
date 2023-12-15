package com.epam.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeUpdateDtoOutput {

    private String username;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @JsonProperty("isActive")
    private Boolean isActive;

    private List<TrainerForTraineeDtoOutput> trainers;
}
