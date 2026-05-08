package com.senac.fiscalizei.service;

import com.senac.fiscalizei.dto.TarefaRequestDTO;
import com.senac.fiscalizei.exception.TarefaException;
import com.senac.fiscalizei.model.Tarefa;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.repository.TarefaRepository;
import com.senac.fiscalizei.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefaService(TarefaRepository tarefaRepository,UsuarioRepository usuarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Tarefa> findByAll(){
        return tarefaRepository.findAll();
    }

    public Tarefa findById(Long id){
        return tarefaRepository.findById(id).orElseThrow(()-> new TarefaException("Id não encontado!"));
    }

    public Tarefa create(TarefaRequestDTO tarefaDto){
        Usuario admin = usuarioRepository.findById(tarefaDto.adminCriadorId())
                .orElseThrow(() -> new TarefaException("Administrador não encontrado!"));

        Usuario atribuido = null;
        if (tarefaDto.usuarioAtribuidoId() != null) {
            atribuido = usuarioRepository.findById(tarefaDto.usuarioAtribuidoId())
                    .orElseThrow(() -> new TarefaException("Usuário atribuído não encontrado!"));
        }

        Tarefa tarefa = new Tarefa(
                tarefaDto.descricao(),
                tarefaDto.recorrencia(),
                atribuido,
                admin
        );

        return tarefaRepository.save(tarefa);
    }

    public Tarefa update(Long id, TarefaRequestDTO tarefaDto) {
        Tarefa tarefaExistente = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaException("Tarefa não encontrada com o ID: " + id));

        Usuario admin = usuarioRepository.findById(tarefaDto.adminCriadorId())
                .orElseThrow(() -> new TarefaException("Administrador não encontrado!"));

        Usuario atribuido = null;
        if (tarefaDto.usuarioAtribuidoId() != null) {
            atribuido = usuarioRepository.findById(tarefaDto.usuarioAtribuidoId())
                    .orElseThrow(() -> new TarefaException("Usuário atribuído não encontrado!"));
        }

        tarefaExistente.setDescricao(tarefaDto.descricao());
        tarefaExistente.setRecorrencia(tarefaDto.recorrencia());
        tarefaExistente.setAdminCriador(admin);
        tarefaExistente.setUsuarioAtribuido(atribuido);


        return tarefaRepository.save(tarefaExistente);
    }


}
