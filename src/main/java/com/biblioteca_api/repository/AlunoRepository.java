package com.biblioteca_api.repository;

import com.biblioteca_api.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    // Filtra por parte do nome, ignorando maiúsculas e minúsculas.
    Page<Aluno> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
