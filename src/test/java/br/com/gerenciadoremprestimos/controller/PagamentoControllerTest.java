package br.com.gerenciadoremprestimos.controller;

import org.junit.jupiter.api.BeforeEach;
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

    private String token;

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

    @Test
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

    @Test
    void inserir_CamposVazios_BadRequest() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PagamentoRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inserir_EmprestimoNaoExiste_NotFound() throws Exception {

        requestDTO.setEmprestimoId(TestUtils.ID_INEXISTENTE);

        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
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
    void buscarPorId_DeveRetornarPagamento() throws Exception {

        mockMvc.perform(get(BASE_URL.concat("/{id}"), pagamento.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emprestimo.id").value(emprestimo.getId()))
                .andExpect(jsonPath("$.valorPago").value(pagamento.getValorPago()))
                .andExpect(jsonPath("$.tipoPagamento").value(pagamento.getTipoPagamento().toString()));
    }

    @Test
    void buscarTodos_DeveRetornarListaDePagamentos() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void remover_DeveRemoverEmprestimo() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), pagamento.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) )
                .andExpect(status().isNoContent());
    }
    
}
