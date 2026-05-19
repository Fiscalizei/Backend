package com.senac.fiscalizei.repository;

import com.senac.fiscalizei.model.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
    List<Evidencia> findByTarefaId(Long tarefaId);
}
