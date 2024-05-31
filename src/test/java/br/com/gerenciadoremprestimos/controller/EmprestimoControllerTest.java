package br.com.gerenciadoremprestimos.controller;

import org.junit.jupiter.api.BeforeEach;
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
    
    private String BASE_URL = "/api/emprestimo";

    private EmprestimoRequestDTO requestDTO;

    private String token;

    @BeforeEach
    void setUp() {
        beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
        beneficiario = beneficiarioRepository.save(beneficiario);
        requestDTO   = EmprestimoUtil.criarEmprestimoRequestDTO(false, beneficiario);
        emprestimo   = EmprestimoUtil.criarEmprestimo(beneficiario, TestUtils.VALOR2000, EmprestimoUtil.PORCENTAGEM30, EmprestimoUtil.DATA_EMPRESTIMO1, EmprestimoUtil.DATA_EMPRESTIMO1.plusMonths(1L), false);
        emprestimo   = emprestimoRepository.save(emprestimo);
        token        = TestUtils.obterToken(mockMvc, objectMapper);
    }

    @Test
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

    @Test
    void inserir_CamposVazios_BadRequest() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EmprestimoRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inserir_BeneficiarioNaoExiste_NotFound() throws Exception {

        requestDTO.setBeneficiarioId(TestUtils.ID_INEXISTENTE);

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
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

    @Test
    void atualizar_IdInvalidoDeveLancarExcecao_BadRequest() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INVALIDO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_IdInexistenteDeveLancarExcecao_NotFound() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INEXISTENTE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorId_DeveRetornarEmprestimo() throws Exception {

        mockMvc.perform(get(BASE_URL.concat("/{id}"), emprestimo.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beneficiario.id").value(beneficiario.getId()))
                .andExpect(jsonPath("$.valorEmprestimo").value(emprestimo.getValorEmprestimo()))
                .andExpect(jsonPath("$.porcentagem").value(emprestimo.getPorcentagem()));
    }

    @Test
    void buscarTodos_DeveRetornarListaDeEmprestimos() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void remover_DeveRemoverEmprestimo() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), emprestimo.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isNoContent());
    }
    
}
