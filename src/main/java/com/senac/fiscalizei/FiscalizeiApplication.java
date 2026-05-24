package com.senac.fiscalizei;

import com.senac.fiscalizei.enums.RecorrenciaTarefa;
import com.senac.fiscalizei.enums.StatusTarefa;
import com.senac.fiscalizei.enums.RoleUsuario;
import com.senac.fiscalizei.model.Evidencia;
import com.senac.fiscalizei.model.Tarefa;
import com.senac.fiscalizei.model.Usuario;
import com.senac.fiscalizei.repository.EvidenciaRepository;
import com.senac.fiscalizei.repository.TarefaRepository;
import com.senac.fiscalizei.repository.UsuarioRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class FiscalizeiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiscalizeiApplication.class, args);
	}

	@Bean
	CommandLineRunner seedByEnvironment(
			UsuarioRepository usuarioRepository,
			TarefaRepository tarefaRepository,
			EvidenciaRepository evidenciaRepository,
			Environment springEnv
	) {
		return args -> {
			String environment = resolveEnvironment(springEnv);

			if ("PROD".equals(environment)) {
				return;
			}

			Usuario admin = ensureUser(usuarioRepository, "Admin", "admin@email.com", RoleUsuario.ADMIN);
			Usuario estoquista = ensureUser(usuarioRepository, "Estoquista", "estoquista@email.com", RoleUsuario.COLABORADOR);

			if ("DEV".equals(environment)) {
				// create two extra collaborators for testing
				ensureUser(usuarioRepository, "Colaborador Um", "colaborador1@email.com", RoleUsuario.COLABORADOR);
				ensureUser(usuarioRepository, "Colaborador Dois", "colaborador2@email.com", RoleUsuario.COLABORADOR);
				seedDemoTasks(tarefaRepository, admin, estoquista);
				seedApprovalEvidence(evidenciaRepository, tarefaRepository, admin, estoquista);
			}
		};
	}

	private String resolveEnvironment(Environment springEnv) {
		String fromProps = springEnv.getProperty("app.environment");
		String environment = firstNonBlank(
				fromProps,
				System.getenv("ENVIRONMENT"),
				System.getenv("ENVIROMENT"),
				System.getProperty("ENVIRONMENT"),
				System.getProperty("ENVIROMENT")
		);
		if (environment == null) {
			return "PROD";
		}

		return environment.trim().toUpperCase();
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
	}

	private Usuario ensureUser(UsuarioRepository usuarioRepository, String nome, String email, RoleUsuario roleUsuario) {
		return usuarioRepository.findByEmail(email)
				.orElseGet(() -> usuarioRepository.save(new Usuario(
						nome,
						email,
						"123",
						roleUsuario,
						true
					)));
	}

	private void seedDemoTasks(TarefaRepository tarefaRepository, Usuario admin, Usuario estoquista) {
		seedTasksIfMissing(tarefaRepository, admin, estoquista, "Tarefa pendente", StatusTarefa.PENDENTE, 5, 0);
		seedTasksIfMissing(tarefaRepository, admin, estoquista, "Tarefa em andamento", StatusTarefa.EM_ANDAMENTO, 5, 5);
		seedTasksIfMissing(tarefaRepository, admin, estoquista, "Tarefa concluida", StatusTarefa.CONCLUIDA, 5, 10);
	}

	private void seedApprovalEvidence(EvidenciaRepository evidenciaRepository, TarefaRepository tarefaRepository, Usuario admin, Usuario estoquista) {
		List<Tarefa> tarefas = tarefaRepository.findByAdminCriadorId(admin.getId());
		for (Tarefa tarefa : tarefas) {
			String nome = tarefa.getNome() == null ? "" : tarefa.getNome().toLowerCase();
			boolean deveTerEvidencia = tarefa.getStatus() == StatusTarefa.EM_ANDAMENTO && nome.startsWith("tarefa em andamento");
			if (!deveTerEvidencia) {
				continue;
			}

			boolean jaTemEvidencia = !evidenciaRepository.findByTarefaId(tarefa.getId()).isEmpty();
			if (jaTemEvidencia) {
				continue;
			}

			Path imagemBase = localizarImagemBase().orElse(null);
			if (imagemBase == null) {
				continue;
			}

			try {
				Path pastaUpload = Paths.get("uploads/evidencias");
				Files.createDirectories(pastaUpload);

				String extensao = obterExtensao(imagemBase.getFileName().toString());
				Path destino = pastaUpload.resolve(tarefa.getId() + "_seed_" + UUID.randomUUID() + extensao);
				Files.copy(imagemBase, destino, StandardCopyOption.REPLACE_EXISTING);

				Evidencia evidencia = new Evidencia(
						tarefa,
						destino.toString(),
						"Evidência fictícia para teste do fluxo de aprovação",
						estoquista,
						admin
				);
				evidenciaRepository.save(evidencia);
			} catch (Exception ignored) {
				// seed only; keep app starting even if local file copy fails
			}
		}
	}

	private Optional<Path> localizarImagemBase() {
		Path[] candidatos = new Path[] {
				Paths.get("../fiscalizei/assetsport/estoque.jpeg"),
				Paths.get("../fiscalizei/assetsport/entregador.jpg.png"),
				Paths.get("assetsport/estoque.jpeg"),
				Paths.get("assetsport/entregador.jpg.png")
		};

		for (Path candidato : candidatos) {
			if (Files.exists(candidato)) {
				return Optional.of(candidato);
			}
		}

		return Optional.empty();
	}

	private String obterExtensao(String nomeOriginal) {
		if (nomeOriginal == null || !nomeOriginal.contains(".")) {
			return ".jpg";
		}
		return nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
	}

	private void seedTasksIfMissing(
			TarefaRepository tarefaRepository,
			Usuario admin,
			Usuario estoquista,
			String prefixo,
			StatusTarefa status,
			int quantidade,
			int deslocamentoDias
	) {
		List<Tarefa> tarefasExistentes = tarefaRepository.findByAdminCriadorId(admin.getId());

		for (int indice = 1; indice <= quantidade; indice++) {
			String nome = prefixo + " " + indice;
			boolean jaExiste = tarefasExistentes.stream().anyMatch(tarefa -> nome.equals(tarefa.getNome()));
			if (jaExiste) {
				continue;
			}

			Tarefa tarefa = new Tarefa(
					nome,
					"Dados fictícios para teste " + nome,
					indice % 2 == 0 ? RecorrenciaTarefa.SEMANAL : RecorrenciaTarefa.DIARIA,
					estoquista,
					admin
			);
			tarefa.setStatus(status);
			tarefa.setDataCriacao(LocalDateTime.now().minusDays(deslocamentoDias + indice));
			tarefaRepository.save(tarefa);
		}
	}

}
