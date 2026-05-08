package com.senac.fiscalizei.controller;

import com.senac.fiscalizei.dto.ApiResponse;
import com.senac.fiscalizei.dto.TarefaRequestDTO;
import com.senac.fiscalizei.model.Tarefa;
import com.senac.fiscalizei.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tarefa")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTodas() {
        List<Tarefa> tarefas = tarefaService.findByAll();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable Long id) {
        Tarefa tarefa = tarefaService.findById(id);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> criar(@RequestBody @Valid TarefaRequestDTO tarefaDto) {
        tarefaService.create(tarefaDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tarefa criada com sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> atualizar(@PathVariable Long id, @RequestBody @Valid TarefaRequestDTO tarefaDto) {
        tarefaService.update(id, tarefaDto);
        return ResponseEntity.ok(ApiResponse.success("Tarefa atualizada com sucesso!"));
    }
}
