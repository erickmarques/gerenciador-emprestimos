package br.com.gerenciadoremprestimos.utils;

import br.com.gerenciadoremprestimos.dto.LoginRequestDTO;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TestUtils {

    public static final int ANO                         = 2024;
    public static final int MES                         = 5;

    public static final Double VALOR1000                = 1000.0;
    public static final Double VALOR2000                = 2000.0;
    public static final Double VALOR3000                = 3000.0;
    public static final Double VALOR_INEXISTENTE        = -9999.0;
    
    public static final String ID_VALIDO                = "1";
    public static final String ID_INVALIDO              = "abc";
    public static final Long ID_INEXISTENTE             = 99999L;
    
    public static String obterToken(MockMvc mockMvc, ObjectMapper objectMapper) {

        try {
            MvcResult result = mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequestDTO("erick_marques", "123"))))
                    .andReturn();
            
            String token = result.getResponse().getContentAsString();

            return token;
        } catch (Exception e) {
            return null;
        }
    }
    
}
