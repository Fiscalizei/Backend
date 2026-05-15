package com.senac.fiscalizei.repository;

import com.senac.fiscalizei.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByUsuarioAtribuidoId(Long usuarioId);
}
