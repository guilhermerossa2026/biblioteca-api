package com.biblioteca_api.controller;

import com.biblioteca_api.dto.EmprestimoAtualizacaoDTO; // Dados aceitos pelo PUT.
import com.biblioteca_api.dto.EmprestimoRequestDTO;
import com.biblioteca_api.dto.EmprestimoResponseDTO;
import com.biblioteca_api.model.StatusEmprestimo; // Tipo do filtro ATIVO ou DEVOLVIDO.
import com.biblioteca_api.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> cadastrar(
            @Valid @RequestBody EmprestimoRequestDTO dados
    ) {
        EmprestimoResponseDTO emprestimo = emprestimoService.cadastrar(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EmprestimoResponseDTO>> listar(
            @RequestParam(name = "status", required = false) StatusEmprestimo status,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        // O Spring converte ?status=ATIVO ou ?status=DEVOLVIDO para o enum.
        return ResponseEntity.ok(new PagedModel<>(emprestimoService.listar(status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.of(emprestimoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EmprestimoAtualizacaoDTO dados
    ) {
        // O DTO do PUT aceita o novo prazo e, opcionalmente, a data de devolução.
        return ResponseEntity.of(emprestimoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (emprestimoService.excluir(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}