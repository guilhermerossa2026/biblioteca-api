package com.biblioteca_api.repository;

import com.biblioteca_api.model.Livro; // Entidade que este repositório gerencia.
import org.springframework.data.jpa.repository.JpaRepository; // persistência
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Integer> {

    // Busca livros cujo título contém o texto informado, ignorando maiúsculas e minúsculas.
    Page<Livro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Bloqueia o registro do livro até terminar o empréstimo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Livro> findOneById(Integer id);
}

