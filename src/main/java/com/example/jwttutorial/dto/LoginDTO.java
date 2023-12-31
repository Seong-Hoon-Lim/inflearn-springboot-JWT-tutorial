package com.example.jwttutorial.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotNull
    @Size(min = 3, max = 50)
    private String membername;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;

}
