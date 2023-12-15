package com.epam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerForTraineeDtoOutput {

    private String username;

    private String firstName;

    private String lastName;

    private TrainingTypeShortOutputDto specialization;
}
