package com.senac.fiscalizei.model;

import com.senac.fiscalizei.enums.RecorrenciaTarefa;
import com.senac.fiscalizei.enums.StatusTarefa;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Tarefa")
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecorrenciaTarefa recorrencia;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTarefa status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_atribuido_id", nullable = true)
    private Usuario usuarioAtribuido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_criador_id", nullable = false)
    private Usuario adminCriador;

    public Tarefa(){}
    public Tarefa(String descricao, RecorrenciaTarefa recorrencia,  Usuario usuarioAtribuido, Usuario adminCriador) {
        this.descricao = descricao;
        this.recorrencia = recorrencia;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusTarefa.PENDENTE;
        this.usuarioAtribuido = usuarioAtribuido;
        this.adminCriador = adminCriador;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public RecorrenciaTarefa getRecorrencia() {
        return recorrencia;
    }

    public void setRecorrencia(RecorrenciaTarefa recorrencia) {
        this.recorrencia = recorrencia;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    public Usuario getUsuarioAtribuido() {
        return usuarioAtribuido;
    }

    public void setUsuarioAtribuido(Usuario usuarioAtribuido) {
        this.usuarioAtribuido = usuarioAtribuido;
    }

    public Usuario getAdminCriador() {
        return adminCriador;
    }

    public void setAdminCriador(Usuario adminCriador) {
        this.adminCriador = adminCriador;
    }
}
