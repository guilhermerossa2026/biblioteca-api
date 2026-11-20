package com.biblioteca_api.dto;

import jakarta.validation.constraints.NotNull; // Exige o prazo previsto no PUT.
import java.time.LocalDate;

public record EmprestimoAtualizacaoDTO(
        @NotNull LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao // Campo opcional para registrar a devolução efetiva.
) {
}