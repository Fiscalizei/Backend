package com.senac.fiscalizei.controller;

import com.senac.fiscalizei.dto.LoginDTO;
import com.senac.fiscalizei.dto.UsuarioDTO;
import com.senac.fiscalizei.dto.UsuarioResponseDTO;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuario")

public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> todosUsuarios() {
        return ResponseEntity.ok(service.listarTodosDto());
    }

    @GetMapping("/colaboradores")
    public ResponseEntity<List<UsuarioResponseDTO>> listarColaboradores() {
        List<UsuarioResponseDTO> colaboradores = service.listarColaboradores();
        return ResponseEntity.ok(colaboradores);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ResponseEntity.ok(service.loginResponse(loginDTO));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarResponse(usuarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(service.atualizarResponse(id, usuarioDTO));
    }

    // Atualiza apenas a foto — recebe multipart/form-data com campo "foto"
    @PatchMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponseDTO> atualizarFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) {
        try {
            return ResponseEntity.ok(service.atualizarFotoResponse(id, foto));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Serve a foto salva em disco — GET /api/usuario/{id}/foto
    // O frontend pode usar essa URL como src da <img>
    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> buscarFoto(@PathVariable Long id) {
        Usuario usuario = service.buscarId(id);

        if (usuario.getFotoPath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path arquivo  = Paths.get(usuario.getFotoPath());
            Resource resource = new UrlResource(arquivo.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Detecta o tipo da imagem automaticamente pelo conteúdo
            String contentType = java.nio.file.Files.probeContentType(arquivo);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}