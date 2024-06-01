package br.com.gerenciadoremprestimos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gerenciadoremprestimos.dto.LoginRequestDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de teste para o LoginController.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO requestDTO;

    private final String BASE_URL = "/login";

    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        requestDTO = new LoginRequestDTO("erick_marques", "123");
    }

    /**
     * Teste para verificar o login com sucesso.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - login com sucesso")
    void login_ComSucesso() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    /**
     * Teste para verificar o login com falha devido a credenciais inválidas.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - login com falha devido a credenciais inválidas")
    void login_ComFalha() throws Exception {
        requestDTO.setSenha("-99999");

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }    
}
