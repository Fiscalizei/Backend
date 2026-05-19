package com.senac.fiscalizei.controller;

import com.senac.fiscalizei.dto.ApiResponse;
import com.senac.fiscalizei.dto.EvidenciaRequestDTO;
import com.senac.fiscalizei.model.Evidencia;
import com.senac.fiscalizei.service.EvidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/evidencia")
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @GetMapping
    public ResponseEntity<List<Evidencia>> listarTodas() {
        List<Evidencia> evidencias = evidenciaService.findByAll();
        return ResponseEntity.ok(evidencias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evidencia> buscarPorId(@PathVariable Long id) {
        Evidencia evidencia = evidenciaService.findById(id);
        return ResponseEntity.ok(evidencia);
    }

    @GetMapping("/tarefa/{tarefaId}")
    public ResponseEntity<List<Evidencia>> buscarPorTarefa(@PathVariable Long tarefaId) {
        List<Evidencia> evidencias = evidenciaService.findByTarefaId(tarefaId);
        return ResponseEntity.ok(evidencias);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> criar(@RequestBody @Valid EvidenciaRequestDTO evidenciaDto) {
        evidenciaService.create(evidenciaDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Evidência criada com sucesso!"));
    }
}
