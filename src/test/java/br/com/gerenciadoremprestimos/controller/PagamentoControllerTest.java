package br.com.gerenciadoremprestimos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import br.com.gerenciadoremprestimos.repository.PagamentoRepository;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.EmprestimoUtil;
import br.com.gerenciadoremprestimos.utils.PagamentoUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;
import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

/**
 * Classe de teste para o PagamentoController.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    private Beneficiario beneficiario;
    
    private Emprestimo emprestimo;

    private Pagamento pagamento;
    
    private final String BASE_URL = "/api/pagamento";

    private PagamentoRequestDTO requestDTO;

    private String token;

    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    void setUp() {
        beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
        beneficiario = beneficiarioRepository.save(beneficiario);
        
        emprestimo   = EmprestimoUtil.criarEmprestimo(beneficiario, TestUtils.VALOR2000, EmprestimoUtil.PORCENTAGEM30, EmprestimoUtil.DATA_EMPRESTIMO1, EmprestimoUtil.DATA_EMPRESTIMO1.plusMonths(1L), false);
        emprestimo   = emprestimoRepository.save(emprestimo);
        
        pagamento    = PagamentoUtil.criarPagamento(emprestimo, TestUtils.VALOR1000, PagamentoUtil.DATA_PAGAMENTO1, TipoPagamento.TOTAL);
        pagamento    = pagamentoRepository.save(pagamento);

        requestDTO   = PagamentoUtil.criarPagamentoRequestDTO(emprestimo);

        token        = TestUtils.obterToken(mockMvc, objectMapper);
    }

    /**
     * Teste para verificar a criação de um pagamento.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve criar um novo pagamento")
    void inserir_DeveCriarPagamento() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.tipoPagamento").value(TipoPagamento.TOTAL.toString()))
                .andExpect(jsonPath("$.valorPago").value(TestUtils.VALOR2000));
    }

    /**
     * Teste para verificar a criação de um pagamento com campos vazios.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar BadRequest ao tentar criar um pagamento com campos vazios")
    void inserir_CamposVazios_BadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PagamentoRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste para verificar a criação de um pagamento com empréstimo inexistente.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar NotFound ao tentar criar um pagamento com empréstimo inexistente")
    void inserir_EmprestimoNaoExiste_NotFound() throws Exception {
        requestDTO.setEmprestimoId(TestUtils.ID_INEXISTENTE);

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste para verificar a atualização de um pagamento.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve atualizar um pagamento existente")
    void atualizar_DeveAtualizarPagamento() throws Exception {
        
        requestDTO.setValorPago(TestUtils.VALOR3000);
        requestDTO.setTipoPagamento(TipoPagamento.JUROS.toString());

        mockMvc.perform(put(BASE_URL.concat("/{id}"), pagamento.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.valorPago").value(requestDTO.getValorPago()))
                .andExpect(jsonPath("$.tipoPagamento").value(requestDTO.getTipoPagamento().toString()));
    }

    /**
     * Teste para verificar a atualização de um pagamento com ID inválido.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar BadRequest ao tentar atualizar um pagamento com ID inválido")
    void atualizar_IdInvalidoDeveLancarExcecao_BadRequest() throws Exception {
        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INVALIDO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste para verificar a atualização de um pagamento com ID inexistente.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar NotFound ao tentar atualizar um pagamento com ID inexistente")
    void atualizar_IdInexistenteDeveLancarExcecao_NotFound() throws Exception {
        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INEXISTENTE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)         
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste para verificar a busca de um pagamento por ID.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar um pagamento ao buscar por ID")
    void buscarPorId_DeveRetornarPagamento() throws Exception {
        mockMvc.perform(get(BASE_URL.concat("/{id}"), pagamento.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.valorPago").value(pagamento.getValorPago()))
                .andExpect(jsonPath("$.tipoPagamento").value(pagamento.getTipoPagamento().toString()));
    }

    /**
     * Teste para verificar a busca de todos os pagamentos.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve retornar uma lista de pagamentos")
    void buscarTodos_DeveRetornarListaDePagamentos() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    /**
     * Teste para verificar a remoção de um pagamento.
     *
     * @throws Exception se ocorrer um erro ao executar a solicitação.
     */
    @Transactional
    @Test
    @DisplayName("Teste de integração do endpoint " + BASE_URL + "/{id} - Deve remover um pagamento existente")
    void remover_DeveRemoverEmprestimo() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), pagamento.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isNoContent());
    }
}
