package com.senac.fiscalizei.controller;

import com.senac.fiscalizei.dto.ApiResponse;
import com.senac.fiscalizei.dto.EvidenciaRequestDTO;
import com.senac.fiscalizei.model.Evidencia;
import com.senac.fiscalizei.service.EvidenciaService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @PatchMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Evidencia> atualizarFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) {
        try {
            return ResponseEntity.ok(evidenciaService.atualizarFoto(id, foto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> buscarFoto(@PathVariable Long id) {
        Evidencia evidencia = evidenciaService.findById(id);

        if (evidencia.getFotoUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path arquivo = Paths.get(evidencia.getFotoUrl());
            Resource resource = new UrlResource(arquivo.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(arquivo);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> criarComFoto(
            @RequestParam("tarefaId") Long tarefaId,
            @RequestParam("foto") MultipartFile foto,
            @RequestParam(value = "usuarioAtribuidoId", required = false) Long usuarioAtribuidoId,
            @RequestParam(value = "adminCriadorId", required = false) Long adminCriadorId,
            @RequestParam(value = "comentario", required = false) String comentario) {
        try {
            evidenciaService.createWithFoto(tarefaId, foto, usuarioAtribuidoId, adminCriadorId, comentario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Evidência enviada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
