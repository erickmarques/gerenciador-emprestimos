package br.com.gerenciadoremprestimos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.EmprestimoUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;
import jakarta.transaction.Transactional;
import static org.hamcrest.Matchers.hasSize;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de teste para o EmprestimoController.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;
    
    private Beneficiario beneficiario;
    
    private Emprestimo emprestimo;
    
    private final String BASE_URL = "/api/emprestimo";

    private EmprestimoRequestDTO requestDTO;

    private String token;

    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
        beneficiario = beneficiarioRepository.save(beneficiario);
        requestDTO   = EmprestimoUtil.criarEmprestimoRequestDTO(false, beneficiario);
        emprestimo   = EmprestimoUtil.criarEmprestimo(beneficiario, TestUtils.VALOR2000, EmprestimoUtil.PORCENTAGEM30, EmprestimoUtil.DATA_EMPRESTIMO1, EmprestimoUtil.DATA_EMPRESTIMO1.plusMonths(1L), false);
        emprestimo   = emprestimoRepository.save(emprestimo);
        token        = TestUtils.obterToken(mockMvc, objectMapper);
    }

    /**
     * Teste para verificar a criação de um empréstimo.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve criar um novo empréstimo")
    void inserir_DeveCriarEmprestimo() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.beneficiario.id").value(beneficiario.getId()))
                .andExpect(jsonPath("$.valorEmprestimo").value(TestUtils.VALOR1000))
                .andExpect(jsonPath("$.porcentagem").value(EmprestimoUtil.PORCENTAGEM20));
    }

    /**
     * Teste para verificar a criação de um empréstimo com campos vazios.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve retornar BadRequest ao tentar criar um empréstimo com campos vazios")
    void inserir_CamposVazios_BadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EmprestimoRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste para verificar a criação de um empréstimo com beneficiário inexistente.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve retornar NotFound ao tentar criar um empréstimo com beneficiário inexistente")
    void inserir_BeneficiarioNaoExiste_NotFound() throws Exception {
        requestDTO.setBeneficiarioId(TestUtils.ID_INEXISTENTE);

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste para verificar a atualização de um empréstimo.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve atualizar um empréstimo existente")
    void atualizar_DeveAtualizarEmprestimo() throws Exception {
        requestDTO.setValorEmprestimo(TestUtils.VALOR3000);
        requestDTO.setPorcentagem(EmprestimoUtil.PORCENTAGEM20);

        mockMvc.perform(put(BASE_URL.concat("/{id}"), emprestimo.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beneficiario.id").value(beneficiario.getId()))
                .andExpect(jsonPath("$.valorEmprestimo").value(emprestimo.getValorEmprestimo()))
                .andExpect(jsonPath("$.porcentagem").value(emprestimo.getPorcentagem()));
    }

    /**
     * Teste para verificar a atualização de um empréstimo com ID inválido.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Te/{id}ste de integração do endpoint " + BASE_URL + " - Deve retornar BadRequest ao tentar atualizar um empréstimo com ID inválido")
    void atualizar_IdInvalidoDeveLancarExcecao_BadRequest() throws Exception {
        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INVALIDO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste para verificar a atualização de um empréstimo com ID inexistente.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Te/{id}ste de integração do endpoint " + BASE_URL + " - Deve retornar NotFound ao tentar atualizar um empréstimo com ID inexistente")
    void atualizar_IdInexistenteDeveLancarExcecao_NotFound() throws Exception {
        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INEXISTENTE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste para verificar a busca de um empréstimo por ID.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Te/{id}ste de integração do endpoint " + BASE_URL + " - Deve retornar um empréstimo ao buscar por ID")
    void buscarPorId_DeveRetornarEmprestimo() throws Exception {
        mockMvc.perform(get(BASE_URL.concat("/{id}"), emprestimo.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beneficiario.id").value(beneficiario.getId()))
                .andExpect(jsonPath("$.valorEmprestimo").value(emprestimo.getValorEmprestimo()))
                .andExpect(jsonPath("$.porcentagem").value(emprestimo.getPorcentagem()));
    }

    /**
     * Teste para verificar a busca de todos os empréstimos.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve retornar uma lista de empréstimos")
    void buscarTodos_DeveRetornarListaDeEmprestimos() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    /**
     * Teste para verificar a remoção de um empréstimo.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Te/{id}ste de integração do endpoint " + BASE_URL + " - Deve remover um empréstimo existente")
    void remover_DeveRemoverEmprestimo() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), emprestimo.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isNoContent());
    }
}
