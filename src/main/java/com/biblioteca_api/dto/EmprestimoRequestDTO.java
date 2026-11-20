package com.biblioteca_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive; // Exige um ID maior que zero.

import java.time.LocalDate; // Data sem horário.

public record EmprestimoRequestDTO(
        @NotNull @Positive Integer alunoId,
        @NotNull @Positive Integer livroId,
        @NotNull LocalDate dataEmprestimo,
        @NotNull LocalDate dataPrevistaDevolucao
) {
}