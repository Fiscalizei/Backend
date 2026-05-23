package com.senac.fiscalizei.dto;

import com.senac.fiscalizei.enums.RoleUsuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        RoleUsuario role
) {}
