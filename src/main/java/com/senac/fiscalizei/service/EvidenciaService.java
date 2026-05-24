package com.senac.fiscalizei.service;

import com.senac.fiscalizei.dto.EvidenciaRequestDTO;
import com.senac.fiscalizei.enums.StatusTarefa;
import com.senac.fiscalizei.exception.EvidenciaException;
import com.senac.fiscalizei.exception.TarefaException;
import com.senac.fiscalizei.model.Evidencia;
import com.senac.fiscalizei.model.Tarefa;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.repository.EvidenciaRepository;
import com.senac.fiscalizei.repository.TarefaRepository;
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
public class EvidenciaService {

    @Value("${upload.evidencia.dir:uploads/evidencias}")
    private String uploadDir;

    private final EvidenciaRepository evidenciaRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public EvidenciaService(EvidenciaRepository evidenciaRepository, TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository) {
        this.evidenciaRepository = evidenciaRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Evidencia> findByAll() {
        return evidenciaRepository.findAll();
    }

    public List<Evidencia> findByTarefaId(Long tarefaId) {
        if (tarefaId == null) {
            throw new EvidenciaException("ID da tarefa não pode ser nulo");
        }
        return evidenciaRepository.findByTarefaId(tarefaId);
    }

    public Evidencia findById(Long id) {
        return evidenciaRepository.findById(id).orElseThrow(() -> new EvidenciaException("Evidência não encontrada com o ID: " + id));
    }

    public Evidencia create(EvidenciaRequestDTO evidenciaDto) {
        Tarefa tarefa = tarefaRepository.findById(evidenciaDto.tarefaId())
                .orElseThrow(() -> new TarefaException("Tarefa não encontrada!"));

        Usuario admin = null;
        if (evidenciaDto.adminCriadorId() != null) {
            admin = usuarioRepository.findById(evidenciaDto.adminCriadorId())
                    .orElseThrow(() -> new EvidenciaException("Administrador não encontrado!"));
        }

        Usuario atribuido = null;
        if (evidenciaDto.usuarioAtribuidoId() != null) {
            atribuido = usuarioRepository.findById(evidenciaDto.usuarioAtribuidoId())
                    .orElseThrow(() -> new EvidenciaException("Usuário atribuído não encontrado!"));
        }

        Evidencia evidencia = new Evidencia(
                tarefa,
                evidenciaDto.fotoUrl(),
                evidenciaDto.comentario(),
                atribuido,
                admin
        );

        return evidenciaRepository.save(evidencia);
    }

    public Evidencia createWithFoto(Long tarefaId, MultipartFile foto, Long usuarioAtribuidoId, Long adminCriadorId, String comentario) throws IOException {
        if (tarefaId == null) {
            throw new EvidenciaException("ID da tarefa não pode ser nulo.");
        }

        if (foto == null || foto.isEmpty()) {
            throw new EvidenciaException("Nenhuma foto enviada.");
        }

        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new EvidenciaException("O arquivo enviado não é uma imagem válida.");
        }

        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaException("Tarefa não encontrada!"));

        Usuario admin = null;
        if (adminCriadorId != null) {
            admin = usuarioRepository.findById(adminCriadorId)
                    .orElseThrow(() -> new EvidenciaException("Administrador não encontrado!"));
        }

        Usuario atribuido = null;
        if (usuarioAtribuidoId != null) {
            atribuido = usuarioRepository.findById(usuarioAtribuidoId)
                    .orElseThrow(() -> new EvidenciaException("Usuário atribuído não encontrado!"));
        }

        Path pastaUpload = Paths.get(uploadDir);
        Files.createDirectories(pastaUpload);

        String extensao = obterExtensao(foto.getOriginalFilename());
        String nomeArquivo = tarefaId + "_" + UUID.randomUUID() + extensao;
        Path caminhoFinal = pastaUpload.resolve(nomeArquivo);

        Files.write(caminhoFinal, foto.getBytes());

        Evidencia evidencia = new Evidencia(
                tarefa,
                caminhoFinal.toString(),
                comentario,
                atribuido,
                admin
        );

        tarefa.setStatus(StatusTarefa.EM_ANDAMENTO);
        tarefaRepository.save(tarefa);

        return evidenciaRepository.save(evidencia);
    }

    public Evidencia atualizarFoto(Long id, MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) {
            throw new EvidenciaException("Nenhuma foto enviada.");
        }

        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new EvidenciaException("O arquivo enviado não é uma imagem válida.");
        }

        Evidencia evidencia = findById(id);

        Path pastaUpload = Paths.get(uploadDir);
        Files.createDirectories(pastaUpload);

        if (evidencia.getFotoUrl() != null) {
            Path fotoAntiga = Paths.get(evidencia.getFotoUrl());
            Files.deleteIfExists(fotoAntiga);
        }

        String extensao = obterExtensao(foto.getOriginalFilename());
        String nomeArquivo = id + "_" + UUID.randomUUID() + extensao;
        Path caminhoFinal = pastaUpload.resolve(nomeArquivo);

        Files.write(caminhoFinal, foto.getBytes());

        evidencia.setFotoUrl(caminhoFinal.toString());
        return evidenciaRepository.save(evidencia);
    }

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) {
            return ".jpg";
        }

        return nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
    }
}
