package br.com.gerenciadoremprestimos.controller;

import br.com.gerenciadoremprestimos.Utils;
import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BeneficiarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BeneficiarioRepository beneficiarioRepository;
    
    private Beneficiario beneficiario;
    
    private String BASE_URL = "/api/beneficiario";

    private BeneficiarioRequestDTO requestDTO;
    
    @BeforeEach
    void setUp() {
        beneficiario = Utils.criarBeneficiario();
      
        beneficiarioRepository.save(beneficiario);

        requestDTO = Utils.criaBeneficiarioRequestDTO();
    }

    @Transactional
    @Test
    void inserir_DeveCriarBeneficiario() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(Utils.NOME_BENEF))
                .andExpect(jsonPath("$.numeroTelefone").value(Utils.FONE_BENEF))
                .andExpect(jsonPath("$.observacao").value(Utils.OBS_BENEF));
    }

    @Transactional
    @Test
    void inserir_LancarExcecao_BadRequest() throws Exception {

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BeneficiarioRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    void atualizar_DeveAtualizarBeneficiario() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), beneficiario.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(Utils.NOME_BENEF))
                .andExpect(jsonPath("$.numeroTelefone").value(Utils.FONE_BENEF))
                .andExpect(jsonPath("$.observacao").value(Utils.OBS_BENEF));
    }

    @Transactional
    @Test
    void atualizar_DeveLancarExcecao_BadRequest() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), Utils.ID_INVALIDO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    void atualizar_DeveLancarExcecao_NotFound() throws Exception {

        mockMvc.perform(put(BASE_URL.concat("/{id}"), Utils.ID_INEXISTENTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void buscarPorId_DeveRetornarBeneficiario() throws Exception {

        mockMvc.perform(get(BASE_URL.concat("/{id}"), beneficiario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(Utils.NOME_BENEF))
                .andExpect(jsonPath("$.numeroTelefone").value(Utils.FONE_BENEF))
                .andExpect(jsonPath("$.observacao").value(Utils.OBS_BENEF));
    }

    @Transactional
    @Test
    void buscarTodos_DeveRetornarListaDeBeneficiarios() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Transactional
    @Test
    void buscarPorNome_DeveRetornarListaDeBeneficiarios() throws Exception {

        String nomeBusca = "MARQUES";
        String nomeBenef = "EDSON MARQUES";

        Beneficiario beneficiario2 = new Beneficiario(nomeBenef, "81977776666");
        Beneficiario beneficiario3 = new Beneficiario("LAURA ANDRADE", "81955556666");

        beneficiarioRepository.save(beneficiario2);
        beneficiarioRepository.save(beneficiario3);

        mockMvc.perform(get(BASE_URL.concat("/buscarPorNome/{nome}"), nomeBusca))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value(Utils.NOME_BENEF))
                .andExpect(jsonPath("$[1].nome").value(nomeBenef));
    }

    @Transactional
    @Test
    void remover_DeveRemoverBeneficiario() throws Exception {
        mockMvc.perform(delete(BASE_URL.concat("/{id}"), beneficiario.getId()))
                .andExpect(status().isNoContent());
    }

}