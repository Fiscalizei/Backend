package com.senac.fiscalizei.service;

import com.senac.fiscalizei.dto.LoginDTO;
import com.senac.fiscalizei.dto.UsuarioDTO;
import com.senac.fiscalizei.dto.UsuarioResponseDTO;
import com.senac.fiscalizei.enums.RoleUsuario;
import com.senac.fiscalizei.exception.UsuarioException;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
public class UsuarioService {

    @Value("${upload.dir:uploads/usuarios}")
    private String uploadDir;

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public List<UsuarioResponseDTO> listarTodosDto() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Usuario buscarEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado com o e-mail: " + email));
    }

    public Usuario buscarId(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsuarioException("Usuário não encontrado!"));
    }

    public List<UsuarioResponseDTO> listarColaboradores() {
        return repository.findByRoleUsuarioOrderByNomeAsc(RoleUsuario.COLABORADOR)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Usuario criar(UsuarioDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new UsuarioException("E-mail já cadastrado no sistema.");
        }

        Usuario usuario = new Usuario(
                dto.nome(),
                dto.email(),
                dto.senha(),
                RoleUsuario.valueOf(dto.role().toUpperCase()), 
                true
        );

        return repository.save(usuario);
    }

    public Usuario atualizar(Long id, UsuarioDTO usuarioDTO) {
        if (!repository.existsById(id)) {
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

    public UsuarioResponseDTO loginResponse(LoginDTO loginDTO) {
        return toResponseDTO(login(loginDTO));
    }

    public UsuarioResponseDTO criarResponse(UsuarioDTO dto) {
        return toResponseDTO(criar(dto));
    }

    public UsuarioResponseDTO atualizarResponse(Long id, UsuarioDTO usuarioDTO) {
        return toResponseDTO(atualizar(id, usuarioDTO));
    }

    public Usuario atualizarFoto(Long id, MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) {
            throw new UsuarioException("Nenhuma foto enviada.");
        }

        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UsuarioException("O arquivo enviado não é uma imagem válida.");
        }

        Usuario usuario = buscarId(id);

        // Garante que a pasta existe, criando se necessário
        Path pastaUpload = Paths.get(uploadDir);
        Files.createDirectories(pastaUpload);

        // Deleta a foto antiga do disco se existir
        if (usuario.getFotoPath() != null) {
            Path fotoAntiga = Paths.get(usuario.getFotoPath());
            Files.deleteIfExists(fotoAntiga);
        }

        // Gera nome único para o arquivo: {id}_{uuid}.{extensao}
        String extensao  = obterExtensao(foto.getOriginalFilename());
        String nomeArquivo = id + "_" + UUID.randomUUID() + extensao;
        Path caminhoFinal  = pastaUpload.resolve(nomeArquivo);

        // Salva o arquivo no disco
        Files.write(caminhoFinal, foto.getBytes());

        // Salva o caminho relativo no banco
        usuario.setFotoPath(caminhoFinal.toString());
        return repository.save(usuario);
    }

    // Extrai a extensão do nome original do arquivo, ex: ".jpg"
    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) return ".jpg";
        return nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
    }

    public UsuarioResponseDTO atualizarFotoResponse(Long id, MultipartFile foto) throws IOException {
        return toResponseDTO(atualizarFoto(id, foto));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

}
