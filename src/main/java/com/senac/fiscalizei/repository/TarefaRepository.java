package com.senac.fiscalizei.repository;

import com.senac.fiscalizei.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}
