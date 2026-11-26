package com.biblioteca_api.dto;

import com.biblioteca_api.model.StatusEmprestimo; //busca os status na classe
import java.time.LocalDate;

public record EmprestimoResponseDTO(
        Integer id,
        Integer alunoId,
        Integer livroId,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        StatusEmprestimo status
) {
}
