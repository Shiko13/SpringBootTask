package com.epam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TraineeSaveDtoOutput {

    private String username;

    private String password;
}
