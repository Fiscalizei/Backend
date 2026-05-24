package com.senac.fiscalizei;

import com.senac.fiscalizei.enums.RoleUsuario;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.repository.UsuarioRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FiscalizeiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiscalizeiApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDefaultUsers(UsuarioRepository usuarioRepository) {
		return args -> {
			if (!usuarioRepository.existsByEmail("admin@email.com")) {
				usuarioRepository.save(new Usuario(
						"Admin",
						"admin@email.com",
						"123",
						RoleUsuario.ADMIN,
						true
				));
			}

			if (!usuarioRepository.existsByEmail("estoquista@email.com")) {
				usuarioRepository.save(new Usuario(
						"Estoquista",
						"estoquista@email.com",
						"123",
						RoleUsuario.COLABORADOR,
						true
				));
			}
		};
	}

}
