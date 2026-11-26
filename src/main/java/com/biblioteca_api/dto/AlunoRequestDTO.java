package com.biblioteca_api.dto;

import jakarta.validation.constraints.Email;    // Valida o formato do e-mail
import jakarta.validation.constraints.NotBlank; // Exige texto

public record AlunoRequestDTO(
        @NotBlank String nome,
        @NotBlank String matricula,
        @NotBlank @Email String email
) {

}
