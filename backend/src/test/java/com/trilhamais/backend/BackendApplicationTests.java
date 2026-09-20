package com.trilhamais.backend;

import com.trilhamais.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve carregar o contexto da aplicacao com sucesso")
    void contextLoads() {
        // Valida que o Spring inicializou todos os beans necessarios
    }

    @Test
    @DisplayName("Deve conectar ao MySQL e injetar o UserRepository com sucesso")
    void testUserRepositoryConnection() {
        assertNotNull(userRepository, "O UserRepository deve ser injetado pelo Spring Data JPA");
        // Executa uma consulta simples para validar a comunicacao real com a tabela no MySQL
        userRepository.count();
    }

}
