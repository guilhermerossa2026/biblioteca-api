package com.biblioteca_api.model;

import jakarta.persistence.Column;         // Configura restrições nas colunas do banco.
import jakarta.persistence.Entity;         
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID no cadastro.
    private Integer id;

    public Integer getId() {
        return id;
    }

    @Column(nullable = false) // torna notnull
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(nullable = false, unique = true) // torna notnull e unico
    private String matricula;


    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Column(nullable = false, unique = true) // torna notnull e unico
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
