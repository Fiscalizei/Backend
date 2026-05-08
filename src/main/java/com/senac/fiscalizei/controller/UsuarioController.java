package com.senac.fiscalizei.controller;

import com.senac.fiscalizei.dto.LoginDTO;
import com.senac.fiscalizei.dto.UsuarioDTO;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuario")

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }
    
    public ResponseEntity<List<Usuario>> todosUsuarios() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody @Valid LoginDTO loginDTO) {
        Usuario usuario = service.login(loginDTO);

        return ResponseEntity.ok(usuario);
    }

    public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(usuarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(service.atualizar(id, usuarioDTO));
    }
}