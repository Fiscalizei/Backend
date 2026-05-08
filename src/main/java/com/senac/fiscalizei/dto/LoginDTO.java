package com.senac.fiscalizei.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO (
        @NotBlank(message = "E-mail do usuário inválido")
        String email,

        @NotBlank(message = "Senha do usuário inválido")
        String senha
) {}
