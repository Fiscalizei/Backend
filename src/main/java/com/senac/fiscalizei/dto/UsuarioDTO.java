package com.senac.fiscalizei.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioDTO(
        @NotBlank(message = "Nome do usuário inválido")
        String nome,

        @NotBlank(message = "Email do usuário inválido")
        String email,

        @NotBlank(message = "Senha do usuário inválido")
        String senha,

        @NotBlank(message = "Cargo do usuário inválido")
        String role,

        String ativo
) {}
