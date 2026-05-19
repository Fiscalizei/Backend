package com.senac.fiscalizei.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EvidenciaRequestDTO(
        
        @NotNull(message = "O ID da tarefa é obrigatório")
        @Positive(message = "O ID da tarefa deve ser maior que zero")
        Long tarefaId,

        @NotBlank(message = "A URL da foto é obrigatória")
        String fotoUrl,

        String comentario,

        @Positive(message = "O ID do usuário atribuído deve ser maior que zero")
        Long usuarioAtribuidoId,

        @Positive(message = "O ID do administrador criador deve ser maior que zero")
        Long adminCriadorId

) {
}
