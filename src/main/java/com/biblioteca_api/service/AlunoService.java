package com.biblioteca_api.service;

import com.biblioteca_api.dto.AlunoRequestDTO;
import com.biblioteca_api.dto.AlunoResponseDTO;
import com.biblioteca_api.model.Aluno;
import com.biblioteca_api.repository.AlunoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.biblioteca_api.repository.EmprestimoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final EmprestimoRepository emprestimoRepository;

    // O Spring fornece o repositório
    public AlunoService(AlunoRepository alunoRepository, EmprestimoRepository emprestimoRepository) {
        this.alunoRepository = alunoRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    // cadastro aluno -> dto
    public AlunoResponseDTO cadastrar(AlunoRequestDTO dados) {
        Aluno aluno = new Aluno();
        preencherDados(aluno, dados);
        return paraDTO(alunoRepository.save(aluno));
    }

    /*

     public List<AlunoResponseDTO> listar() {
         return alunoRepository.findAll()
                 .stream()
                 .map(this::paraDTO)
                 .toList();
     }
     */

    // busca a página com o filtro opcional por nome.
    public Page<AlunoResponseDTO> listar(String nome, Pageable pageable) {
        Page<Aluno> alunos;

        if (nome == null || nome.isBlank()) {
            alunos = alunoRepository.findAll(pageable);
        } else {
            alunos = alunoRepository.findByNomeContainingIgnoreCase(nome.strip(), pageable);
        }

        return alunos.map(this::paraDTO);
    }

    // consulta  pelo ID.
    public Optional<AlunoResponseDTO> buscarPorId(Integer id) {
        return alunoRepository.findById(id).map(this::paraDTO);
    }

    // atualiza preservando o ID.
    public Optional<AlunoResponseDTO> atualizar(Integer id, AlunoRequestDTO dados) {
        return alunoRepository.findById(id).map(aluno -> {
            preencherDados(aluno, dados);
            return paraDTO(alunoRepository.save(aluno));
        });
    }

    // Deleta se o ID existir.
    public boolean excluir(Integer id) {
        Optional<Aluno> aluno = alunoRepository.findById(id);

        if (aluno.isEmpty()) {
            return false; // O controller devolve 404.
        }

        // Verifica qualquer empréstimo vinculado, inclusive os devolvidos.
        if (emprestimoRepository.existsByAluno_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O aluno tem empréstimos vinculados e não pode ser excluído."
            );
        }

        alunoRepository.delete(aluno.get());
        return true; // O controller devolve 204.
    }

    // Cadastro e atualização preenchem os mesmos campos.
    private void preencherDados(Aluno aluno, AlunoRequestDTO dados) {
        aluno.setNome(dados.nome());
        aluno.setMatricula(dados.matricula());
        aluno.setEmail(dados.email());
    }

    // Define em um só lugar os campos que saem na resposta.
    private AlunoResponseDTO paraDTO(Aluno aluno) {
        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getMatricula(),
                aluno.getEmail()
        );
    }
}
