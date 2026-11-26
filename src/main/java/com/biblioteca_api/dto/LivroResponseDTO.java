package com.biblioteca_api.dto;

// Resposta ao cliente
public record LivroResponseDTO(
        Integer id,
        String titulo,
        String autor,
        Integer quantidadeTotal
) {
}
