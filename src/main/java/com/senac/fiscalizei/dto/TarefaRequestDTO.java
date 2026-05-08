package com.senac.fiscalizei.dto;

import com.senac.fiscalizei.enums.RecorrenciaTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TarefaRequestDTO(

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "A recorrência é obrigatória")
        RecorrenciaTarefa recorrencia,

        @NotNull(message = "ID do Estoquista é obrigatório")
        @Positive(message = "ID do usuário atribuído deve ser maior que zero")
        Long usuarioAtribuidoId,

        @NotNull(message = "ID do administrador criador é obrigatório")
        @Positive(message = "ID do admin criador deve ser maior que zero")
        Long adminCriadorId

) {
}
