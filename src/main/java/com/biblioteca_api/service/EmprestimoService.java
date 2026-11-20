package com.biblioteca_api.service;

import com.biblioteca_api.dto.EmprestimoRequestDTO;
import com.biblioteca_api.dto.EmprestimoResponseDTO;
import com.biblioteca_api.model.Aluno;
import com.biblioteca_api.model.Emprestimo;
import com.biblioteca_api.model.Livro;
import com.biblioteca_api.model.StatusEmprestimo;
import com.biblioteca_api.repository.AlunoRepository;
import com.biblioteca_api.repository.EmprestimoRepository;
import com.biblioteca_api.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.biblioteca_api.dto.EmprestimoAtualizacaoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final AlunoRepository alunoRepository;
    private final LivroRepository livroRepository;

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            AlunoRepository alunoRepository,
            LivroRepository livroRepository
    ) {
        this.emprestimoRepository = emprestimoRepository;
        this.alunoRepository = alunoRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional // Mantém a busca com bloqueio, a contagem e a gravação na mesma transação.
    public EmprestimoResponseDTO cadastrar(EmprestimoRequestDTO dados) {
        if (dados.dataPrevistaDevolucao().isBefore(dados.dataEmprestimo())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "A devolução prevista deve ser igual ou posterior à data do empréstimo."
            );
        }

        Aluno aluno = alunoRepository.findById(dados.alunoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno não encontrado."
                ));

        Livro livro = livroRepository.findOneById(dados.livroId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livro não encontrado."
                ));

        long emprestimosAtivos = emprestimoRepository.countByLivro_IdAndStatus(
                livro.getId(), StatusEmprestimo.ATIVO
        );

        if (emprestimosAtivos >= livro.getQuantidadeTotal()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Não há exemplar disponível para empréstimo."
            );
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setAluno(aluno);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(dados.dataEmprestimo());
        emprestimo.setDataPrevistaDevolucao(dados.dataPrevistaDevolucao());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        return paraDTO(emprestimoRepository.save(emprestimo));
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponseDTO> listar(StatusEmprestimo status, Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        Page<Emprestimo> emprestimos;

        if (status == null) {
            emprestimos = emprestimoRepository.findAll(pageable);
        } else {
            emprestimos = switch (status) {
                case ATIVO -> emprestimoRepository
                        .findByStatusAndDataPrevistaDevolucaoGreaterThanEqual(
                                StatusEmprestimo.ATIVO, hoje, pageable
                        );
                case ATRASADO -> emprestimoRepository
                        .findByStatusAndDataPrevistaDevolucaoBefore(
                                StatusEmprestimo.ATIVO, hoje, pageable
                        );
                case DEVOLVIDO -> emprestimoRepository
                        .findByStatus(StatusEmprestimo.DEVOLVIDO, pageable);
            };
        }

        return emprestimos.map(emprestimo -> paraDTO(emprestimo, hoje));
    }

    @Transactional(readOnly = true)
    public Optional<EmprestimoResponseDTO> buscarPorId(Integer id) {
        return emprestimoRepository.findById(id).map(this::paraDTO);
    }

    @Transactional
    public Optional<EmprestimoResponseDTO> atualizar(
            Integer id, EmprestimoAtualizacaoDTO dados
    ) {
        return emprestimoRepository.findOneById(id).map(emprestimo -> {
            LocalDate dataInicial = emprestimo.getDataEmprestimo();
            LocalDate dataPrevista = dados.dataPrevistaDevolucao();
            LocalDate dataDevolucao = dados.dataDevolucao();

            if (dataPrevista.isBefore(dataInicial)) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "A devolução prevista deve ser igual ou posterior à data do empréstimo."
                );
            }

            if (dataDevolucao != null && dataDevolucao.isBefore(dataInicial)) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "A devolução efetiva deve ser igual ou posterior à data do empréstimo."
                );
            }

            // Preserva a devolução já registrada se o PUT alterar apenas o prazo.
            if (dataDevolucao == null) {
                dataDevolucao = emprestimo.getDataDevolucao();
            }

            emprestimo.setDataPrevistaDevolucao(dataPrevista);
            emprestimo.setDataDevolucao(dataDevolucao);

            // A devolução libera o exemplar porque a contagem considera apenas os ATIVOS.
            if (dataDevolucao != null) {
                emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
            }

            return paraDTO(emprestimoRepository.save(emprestimo));
        });
    }

    @Transactional
    public boolean excluir(Integer id) {
        Optional<Emprestimo> emprestimo = emprestimoRepository.findOneById(id);

        if (emprestimo.isEmpty()) {
            return false;
        }

        emprestimoRepository.delete(emprestimo.get());
        return true;
    }

    private EmprestimoResponseDTO paraDTO(Emprestimo emprestimo) {
        return paraDTO(emprestimo, LocalDate.now());
    }

    private EmprestimoResponseDTO paraDTO(Emprestimo emprestimo, LocalDate hoje) {
        StatusEmprestimo status = emprestimo.getStatus();

        // Calcula o atraso para a resposta, mantendo o empréstimo em aberto no banco.
        if (status == StatusEmprestimo.ATIVO
                && emprestimo.getDataPrevistaDevolucao().isBefore(hoje)) {
            status = StatusEmprestimo.ATRASADO;
        }

        return new EmprestimoResponseDTO(
                emprestimo.getId(),
                emprestimo.getAluno().getId(),
                emprestimo.getLivro().getId(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao(),
                status
        );
    }
}