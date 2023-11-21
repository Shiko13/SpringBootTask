package com.epam.spec.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TrainingTraineeFilter {

    @NotBlank
    String username;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate periodFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate periodTo;

    String trainerName;

    String trainingType;
}
