package com.biblioteca_api.service;

import com.biblioteca_api.dto.LivroRequestDTO;  // Requisição
import com.biblioteca_api.dto.LivroResponseDTO; // Resposta
import com.biblioteca_api.model.Livro;         // Entidade
import com.biblioteca_api.repository.LivroRepository; // Acesso aos livros no banco
import org.springframework.stereotype.Service; // Permite que o Spring gerencie esta classe
import java.util.Optional;
//import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.biblioteca_api.repository.EmprestimoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.biblioteca_api.model.StatusEmprestimo;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    // marca o respositorio referente ao serviço, fornece o mesmo para uso
    public LivroService(LivroRepository livroRepository, EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public LivroResponseDTO cadastrar(LivroRequestDTO dados) {
        // Copia os dados do DTO de entrada para a entidade.
        Livro livro = new Livro();
        livro.setTitulo(dados.titulo());
        livro.setAutor(dados.autor());
        livro.setQuantidadeTotal(dados.quantidadeTotal());

        // Guarda o resultado da gravação, que já contém o ID gerado.
        Livro livroSalvo = livroRepository.save(livro);

        // Monta o DTO que será devolvido ao cliente.
        return new LivroResponseDTO(
                livroSalvo.getId(),
                livroSalvo.getTitulo(),
                livroSalvo.getAutor(),
                livroSalvo.getQuantidadeTotal()
        );
    }

    /*public List<LivroResponseDTO> listar() {
        // Busca os livros no banco e transforma cada um em um DTO de resposta.
        return livroRepository.findAll()
                .stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getTitulo(),
                        livro.getAutor(),
                        livro.getQuantidadeTotal()
                ))
                .toList();
    } */

    //busca por titulo e paginação
    public Page<LivroResponseDTO> listar(String titulo, Pageable pageable) {
        Page<Livro> livros;

        if (titulo == null || titulo.isBlank()) {
            livros = livroRepository.findAll(pageable);
        } else {
            livros = livroRepository.findByTituloContainingIgnoreCase(titulo, pageable);
        }

        return livros.map(livro -> new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getQuantidadeTotal()
        ));
    }

    public Optional<LivroResponseDTO> buscarPorId(Integer id) {
        // Procura pelo ID e monta o DTO
        return livroRepository.findById(id)
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getTitulo(),
                        livro.getAutor(),
                        livro.getQuantidadeTotal()
                ));
    }

    //metodo de atualização
    @Transactional
    public Optional<LivroResponseDTO> atualizar(Integer id, LivroRequestDTO dados) {
        return livroRepository.findOneById(id).map(livro -> {
            // Conta os exemplares emprestados, incluindo os atrasados.
            long emAberto = emprestimoRepository.countByLivro_IdAndStatus(
                    id, StatusEmprestimo.ATIVO
            );

            // Verifica o novo total antes de alterar os campos do livro.
            if (dados.quantidadeTotal() < emAberto) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A quantidade total não pode ser menor que a quantidade emprestada."
                );
            }

            livro.setTitulo(dados.titulo());
            livro.setAutor(dados.autor());
            livro.setQuantidadeTotal(dados.quantidadeTotal());

            Livro livroAtualizado = livroRepository.save(livro);

            return new LivroResponseDTO(
                    livroAtualizado.getId(),
                    livroAtualizado.getTitulo(),
                    livroAtualizado.getAutor(),
                    livroAtualizado.getQuantidadeTotal()
            );
        });
    }

    // metodo de exclusão
    public boolean excluir(Integer id) {
        Optional<Livro> livro = livroRepository.findById(id);

        if (livro.isEmpty()) {
            return false; // O controller devolve 404.
        }

        // Verifica qualquer empréstimo vinculado, inclusive os devolvidos.
        if (emprestimoRepository.existsByLivro_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O livro tem empréstimos vinculados e não pode ser excluído."
            );
        }

        livroRepository.delete(livro.get());
        return true; // O controller devolve 204.
    }
}