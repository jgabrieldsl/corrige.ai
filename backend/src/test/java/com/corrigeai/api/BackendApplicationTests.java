package com.corrigeai.api; // Pacote corrigido

import com.corrigeai.api.models.RegistroConexao;
import com.corrigeai.api.repositories.RegistroConexaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

// TESTE APENAS PARA VER SE ESTÁ FUNCIONANDO!

@SpringBootTest // Diz ao Spring para carregar a aplicação inteira para este teste
class ApiApplicationTests {

	// 1. Injeta o repositório que você quer testar
	@Autowired
	private RegistroConexaoRepository repository;

	@BeforeEach
	void setUp() {
		System.out.println("Limpando banco de dados de teste...");
		repository.deleteAll();
	}


	@Test
	void deveConectarSalvarEBuscarNoMongoDB() {
		System.out.println("--- INICIANDO TESTE DE CONEXÃO COM MONGODB ---");

		// 3. Cria o objeto de teste
		String socketIdTeste = "socket123-teste-unitario";
		Long timestampTeste = 1698948600000L;
		RegistroConexao novoRegistro = new RegistroConexao(socketIdTeste, timestampTeste);

		// 4. Salva no MongoDB Atlas
		System.out.println("Salvando registro...");
		RegistroConexao registroSalvo = repository.save(novoRegistro);

		// 5. Busca no banco para provar que salvou
		System.out.println("Buscando registro...");
		List<RegistroConexao> todosOsRegistros = repository.findAll();

		// 6. VERIFICAÇÃO
		System.out.println("Verificando se o registro foi salvo...");
		Assertions.assertNotNull(registroSalvo.getId()); // Prova que o banco deu um ID
		Assertions.assertEquals(1, todosOsRegistros.size()); // Prova que tem 1 item na lista
		Assertions.assertEquals(socketIdTeste, todosOsRegistros.get(0).getSocketId()); // Prova que o dado é o mesmo

		System.out.println("--- TESTE CONCLUÍDO COM SUCESSO ---");
	}

}
