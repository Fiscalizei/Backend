package com.senac.fiscalizei.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="evidencias")
public class Evidencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @Column(name = "foto_url", nullable = false, length = 255)
    private String fotoUrl;

    @Column(name = "timestamp_envio", nullable = false)
    private LocalDateTime timestampEnvio;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_atribuido_id")
    private Usuario usuarioAtribuido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_criador_id")
    private Usuario adminCriador;

    public Evidencia() {}

    public Evidencia(Tarefa tarefa, String fotoUrl, String comentario, Usuario usuarioAtribuido, Usuario adminCriador) {
        this.tarefa = tarefa;
        this.fotoUrl = fotoUrl;
        this.comentario = comentario;
        this.usuarioAtribuido = usuarioAtribuido;
        this.adminCriador = adminCriador;
        this.timestampEnvio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public LocalDateTime getTimestampEnvio() {
        return timestampEnvio;
    }

    public void setTimestampEnvio(LocalDateTime timestampEnvio) {
        this.timestampEnvio = timestampEnvio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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
