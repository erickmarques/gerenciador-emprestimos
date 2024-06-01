package br.com.gerenciadoremprestimos.controller;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Classe de teste de integração do BeneficiarioController.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BeneficiarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private BeneficiarioRepository beneficiarioRepository;
    
    private final String BASE_URL = "/api/beneficiario";

    private Beneficiario beneficiario;

    private BeneficiarioRequestDTO requestDTO;

    private String token;

    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
	  
        beneficiarioRepository.save(beneficiario);

        requestDTO = BeneficiarioUtil.criaBeneficiarioRequestDTO();

        token = TestUtils.obterToken(mockMvc, objectMapper);
    }

    /**
     * Teste para verificar a criação de um beneficiário.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve criar um novo beneficiário")
    void inserir_DeveCriarBeneficiario() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(BeneficiarioUtil.NOME_BENEF))
                .andExpect(jsonPath("$.numeroTelefone").value(BeneficiarioUtil.FONE_BENEF))
                .andExpect(jsonPath("$.observacao").value(BeneficiarioUtil.OBS_BENEF));
    }

    /**
     * Teste para verificar a resposta ao enviar uma requisição inválida de criação de beneficiário.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve lançar exceção BadRequest ao criar beneficiário inválido")
    void inserir_LancarExcecao_BadRequest() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BeneficiarioRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste para verificar a atualização de um beneficiário.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve atualizar um beneficiário existente")
    void atualizar_DeveAtualizarBeneficiario() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), beneficiario.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(BeneficiarioUtil.NOME_BENEF))
                .andExpect(jsonPath("$.numeroTelefone").value(BeneficiarioUtil.FONE_BENEF))
                .andExpect(jsonPath("$.observacao").value(BeneficiarioUtil.OBS_BENEF));
    }

    /**
     * Teste para verificar a resposta ao tentar atualizar um beneficiário com dados inválidos.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve lançar exceção BadRequest ao atualizar beneficiário inválido")
    void atualizar_DeveLancarExcecao_BadRequest() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INVALIDO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste para verificar a resposta ao tentar atualizar um beneficiário inexistente.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve lançar exceção NotFound ao atualizar beneficiário inexistente")
    void atualizar_DeveLancarExcecao_NotFound() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INEXISTENTE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste para verificar a busca de um beneficiário por ID.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar um beneficiário pelo ID")
    void buscarPorId_DeveRetornarBeneficiario() throws Exception {

        mockMvc.perform(get(BASE_URL.concat("/{id}"), beneficiario.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(BeneficiarioUtil.NOME_BENEF))
                .andExpect(jsonPath("$.numeroTelefone").value(BeneficiarioUtil.FONE_BENEF))
                .andExpect(jsonPath("$.observacao").value(BeneficiarioUtil.OBS_BENEF));
    }

    /**
     * Teste para verificar a busca de todos os beneficiários.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve retornar a lista de todos os beneficiários")
    void buscarTodos_DeveRetornarListaDeBeneficiarios() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    /**
     * Teste para verificar a busca de beneficiários por nome.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/buscarPorNome/{nome} - Deve retornar a lista de beneficiários pelo nome")
    void buscarPorNome_DeveRetornarListaDeBeneficiarios() throws Exception {

        mockMvc.perform(get(BASE_URL.concat("/buscarPorNome/{nome}"), BeneficiarioUtil.NOME_PESQUISA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome").value(BeneficiarioUtil.NOME_BENEF))
                .andExpect(jsonPath("$[1].numeroTelefone").value(BeneficiarioUtil.FONE_BENEF));
    }

    /**
     * Teste para verificar a remoção de um beneficiário.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + " - Deve remover um beneficiário existente")
    void remover_DeveRemoverBeneficiario() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), beneficiario.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());
    }

}
