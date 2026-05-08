package com.senac.fiscalizei.service;

import com.senac.fiscalizei.dto.LoginDTO;
import com.senac.fiscalizei.dto.UsuarioDTO;
import com.senac.fiscalizei.enums.RoleUsuario;
import com.senac.fiscalizei.exception.UsuarioException;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com o e-mail: " + email));
    }

    public Usuario buscarId(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsuarioException("Usuário não encontrado!"));
    }

    public Usuario criar(UsuarioDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new UsuarioException("E-mail já cadastrado no sistema.");
        }

        Usuario usuario = new Usuario(
                dto.nome(),
                dto.email(),
                dto.senha(),
                RoleUsuario.valueOf(dto.role().toUpperCase()), // Converte "admin" para Role.ADMIN
                true
        );

        return repository.save(usuario);
    }

    public Usuario atualizar(Long id, UsuarioDTO usuarioDTO) {
        if (repository.existsById(id)) {
            throw new UsuarioException("Usuário não encontrado!");
        }

        Usuario usuario = buscarId(id);

        usuario.setNome(usuarioDTO.nome());
        usuario.setEmail(usuarioDTO.email());
        usuario.setRole(RoleUsuario.valueOf(usuarioDTO.role()));
        usuario.setAtivo(usuario.isAtivo());

        return repository.save(usuario);
    }

    public void deletar(Long id) {
        buscarId(id);

        repository.deleteById(id);
    }

    public Usuario login(LoginDTO loginDTO) {
        Usuario usuario = buscarEmail(loginDTO.email());

        if (!usuario.getSenha().equals(loginDTO.senha())) {
            throw new UsuarioException("Senha inválida!");
        }
        return usuario;
    }
}
