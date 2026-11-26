package com.biblioteca_api.controller;

import com.biblioteca_api.dto.AlunoRequestDTO;
import com.biblioteca_api.dto.AlunoResponseDTO;
import com.biblioteca_api.service.AlunoService;
import jakarta.validation.Valid; // validações de banco
import org.springframework.dao.DataIntegrityViolationException; //Define conflitos de UK no banco
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> cadastrar(
            @Valid @RequestBody AlunoRequestDTO dados
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.cadastrar(dados));
    }

    @GetMapping
    public ResponseEntity<PagedModel<AlunoResponseDTO>> listar(
            @RequestParam(name = "nome", required = false) String nome,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedModel<>(alunoService.listar(nome, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.of(alunoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AlunoRequestDTO dados
    ) {
        return ResponseEntity.of(alunoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (alunoService.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Converte conflitos com restrições do banco em uma resposta HTTP 409.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarConflito() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "A operação viola uma restrição do cadastro de alunos."));
    }
}