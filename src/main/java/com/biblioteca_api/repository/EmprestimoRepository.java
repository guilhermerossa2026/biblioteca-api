package com.biblioteca_api.repository;

import com.biblioteca_api.model.Emprestimo;
import com.biblioteca_api.model.StatusEmprestimo;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.time.LocalDate;

import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {

    // Filtra os empréstimos pelo status
    Page<Emprestimo> findByStatus(StatusEmprestimo status, Pageable pageable);

    // Livro_Id representa o caminho emprestimo.livro.id.
    long countByLivro_IdAndStatus(Integer livroId, StatusEmprestimo status);

    // Bloqueia o empréstimo durante a atualização ou exclusão.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Emprestimo> findOneById(Integer id);

    // Busca empréstimos em aberto com vencimento anterior a hoje.
    Page<Emprestimo> findByStatusAndDataPrevistaDevolucaoBefore(
            StatusEmprestimo status, LocalDate hoje, Pageable pageable
    );

    // Busca empréstimos em aberto que vencem hoje ou depois.
    Page<Emprestimo> findByStatusAndDataPrevistaDevolucaoGreaterThanEqual(
            StatusEmprestimo status, LocalDate hoje, Pageable pageable
    );

    // Verifica se há histórico de empréstimo vinculado ao aluno ou ao livro.
    boolean existsByAluno_Id(Integer alunoId);
    boolean existsByLivro_Id(Integer livroId);
}