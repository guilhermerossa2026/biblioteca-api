package com.biblioteca_api.dto;

// Dados retornados
public record AlunoResponseDTO(
        Integer id,
        String nome,
        String matricula,
        String email
) {
}
