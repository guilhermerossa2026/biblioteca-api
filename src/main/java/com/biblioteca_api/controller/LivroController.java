package com.biblioteca_api.controller;

import com.biblioteca_api.dto.LivroRequestDTO;
import com.biblioteca_api.dto.LivroResponseDTO;
import com.biblioteca_api.service.LivroService;
import jakarta.validation.Valid; // Aciona as validações do DTO de entrada.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Permite definir o status e o corpo da resposta.
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
//import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;


@RestController // define como controlador para p Spring
@RequestMapping("/api/livros") // Define o endereço base das rotas de livros.
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping // metodo para post
    public ResponseEntity<LivroResponseDTO> cadastrar(
            @Valid @RequestBody LivroRequestDTO dados //valida as regras que definimos no DTO
    ) {
        LivroResponseDTO livroCadastrado = livroService.cadastrar(dados);

        return ResponseEntity.status(HttpStatus.CREATED).body(livroCadastrado);
    }

    /* @GetMapping // Metodo consulta
    public ResponseEntity<List<LivroResponseDTO>> listar() {
        return ResponseEntity.ok(livroService.listar());
    } */

    @GetMapping // consulta levando em consideração titulo e paginação
    public ResponseEntity<PagedModel<LivroResponseDTO>> listar(
            @RequestParam(name = "titulo", required = false) String titulo,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedModel<>(livroService.listar(titulo, pageable)));
    }

    @GetMapping("/{id}") // Consulta por /{id}.
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.of(livroService.buscarPorId(id));
    }

    @PutMapping("/{id}") // Atualiza por /{id}.
    public ResponseEntity<LivroResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody LivroRequestDTO dados
    ) {
        return ResponseEntity.of(livroService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}") //Deleta por /{id}.
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (livroService.excluir(id)) {
            return ResponseEntity.noContent().build(); // 204 sem corpo.
        }
        return ResponseEntity.notFound().build(); // 404 para ID inexistente.
    }

    //tratamento usado para não deixar excluir livro que tem registro de emprestimo, mesmo que disponivel
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> tratarConflitoDeIntegridade() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}