package com.biblioteca_api.dto;

import jakarta.validation.constraints.NotBlank; // Exige texto com algum conteúdo
import jakarta.validation.constraints.NotNull; // Exige que o valor
import jakarta.validation.constraints.Positive; // Aceita número positivo

// Dados de cadastro
public record LivroRequestDTO(
        @NotBlank String titulo,
        @NotBlank String autor,
        @NotNull @Positive Integer quantidadeTotal
) {
}
