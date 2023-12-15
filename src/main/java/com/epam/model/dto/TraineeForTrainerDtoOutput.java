package com.epam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TraineeForTrainerDtoOutput {

    private String username;

    private String firstName;

    private String lastName;
}
