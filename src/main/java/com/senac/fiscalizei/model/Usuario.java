package com.senac.fiscalizei.model;

import com.senac.fiscalizei.enums.RoleUsuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column()
    String nome;

    @Column()
    String email;

    @Column()
    String senha;

    @Enumerated(EnumType.STRING)
    @Column()
    RoleUsuario roleUsuario;

    @Column(name = "data_criacao")
    LocalDateTime dataCriacao;

    @Column()
    boolean ativo;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, RoleUsuario roleUsuario, boolean ativo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.roleUsuario = roleUsuario;
        this.ativo = ativo;
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public RoleUsuario getRole() {
        return roleUsuario;
    }

    public void setRole(RoleUsuario roleUsuario) {
        this.roleUsuario = roleUsuario;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
