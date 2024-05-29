package br.com.gerenciadoremprestimos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gerenciadoremprestimos.TestUtils;
import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import br.com.gerenciadoremprestimos.repository.PagamentoRepository;
import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

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
    
    private String BASE_URL = "/api/pagamento";

    private PagamentoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        beneficiario = TestUtils.criarBeneficiario();
        beneficiario = beneficiarioRepository.save(beneficiario);
        
        emprestimo   = TestUtils.criarEmprestimo(beneficiario, TestUtils.VALOR2000, TestUtils.PORCENTAGEM30, TestUtils.DATA_EMPRESTIMO1, TestUtils.DATA_EMPRESTIMO1.plusMonths(1L), false);
        emprestimo   = emprestimoRepository.save(emprestimo);
        
        pagamento    = TestUtils.criarPagamento(emprestimo, TestUtils.VALOR1000, TestUtils.DATA_EMPRESTIMO2, TipoPagamento.TOTAL);
        pagamento    = pagamentoRepository.save(pagamento);

        requestDTO   = TestUtils.criarPagamentoRequestDTO(emprestimo);
    }

    @Transactional
    @Test
    void inserir_DeveCriarPagamento() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.tipoPagamento").value(TipoPagamento.TOTAL.toString()))
                .andExpect(jsonPath("$.valorPago").value(TestUtils.VALOR2000));
    }

    @Transactional
    @Test
    void inserir_CamposVazios_BadRequest() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PagamentoRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    void inserir_EmprestimoNaoExiste_NotFound() throws Exception {

        requestDTO.setEmprestimoId(TestUtils.ID_INEXISTENTE);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void atualizar_DeveAtualizarPagamento() throws Exception {

        requestDTO.setValorPago(TestUtils.VALOR3000);
        requestDTO.setTipoPagamento(TipoPagamento.JUROS.toString());

        mockMvc.perform(put(BASE_URL.concat("/{id}"), pagamento.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.valorPago").value(requestDTO.getValorPago()))
                .andExpect(jsonPath("$.tipoPagamento").value(requestDTO.getTipoPagamento().toString()));
    }

    @Transactional
    @Test
    void atualizar_IdInvalidoDeveLancarExcecao_BadRequest() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INVALIDO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    void atualizar_IdInexistenteDeveLancarExcecao_NotFound() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), TestUtils.ID_INEXISTENTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void buscarPorId_DeveRetornarPagamento() throws Exception {

        mockMvc.perform(get(BASE_URL.concat("/{id}"), pagamento.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.valorPago").value(pagamento.getValorPago()))
                .andExpect(jsonPath("$.tipoPagamento").value(pagamento.getTipoPagamento().toString()));
    }

    @Transactional
    @Test
    void buscarTodos_DeveRetornarListaDePagamentos() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Transactional
    @Test
    void remover_DeveRemoverEmprestimo() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), pagamento.getId()))
                .andExpect(status().isNoContent());
    }
    
}
