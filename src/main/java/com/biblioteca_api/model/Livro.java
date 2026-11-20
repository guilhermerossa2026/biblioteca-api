package com.biblioteca_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // O JPA mapeia esta classe para uma tabela no banco.
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID ao inserir o registro.
    private Integer id;

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    private String titulo;
    private String autor;
    private Integer quantidadeTotal;
}
