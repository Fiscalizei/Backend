package com.senac.fiscalizei.repository;

import com.senac.fiscalizei.enums.RoleUsuario;
import com.senac.fiscalizei.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = "Email do usuário inválido") String email);
    List<Usuario> findByRoleUsuarioOrderByNomeAsc(RoleUsuario roleUsuario);

}
