package br.com.gerenciadoremprestimos;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.dto.LoginRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoResponseDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TestUtils {

    public static final int ANO                         = 2024;
    public static final int MES                         = 5;
    public static final Double PORCENTAGEM30            = 30.0;
    public static final Double PORCENTAGEM20            = 20.0;
    public static final Double VALOR1000                = 1000.0;
    public static final Double VALOR2000                = 2000.0;
    public static final Double VALOR3000                = 3000.0;
    public static final Double VALOR_INEXISTENTE        = -9999.0;
    public static final LocalDateTime DATA_EMPRESTIMO1  = LocalDateTime.of(ANO, MES, 1, 0, 0);
    public static final LocalDateTime DATA_EMPRESTIMO2  = LocalDateTime.of(ANO, MES, 20, 0, 0);
    
    public static final String NOME_BENEF               = "Erick Marques";
    public static final String FONE_BENEF               = "081988888888";
    public static final String OBS_BENEF                = "Observação de teste";

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
 
    public static Beneficiario criarBeneficiario(){
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome(NOME_BENEF);
        beneficiario.setNumeroTelefone(FONE_BENEF);
        beneficiario.setObservacao(OBS_BENEF);

        return beneficiario;
    }

    public static Beneficiario criarBeneficiario(String nome, String telefone, String observacao){
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome(nome);
        beneficiario.setNumeroTelefone(telefone);
        beneficiario.setObservacao(observacao);
        return beneficiario;
    }

    public static BeneficiarioRequestDTO criaBeneficiarioRequestDTO(){
        
        BeneficiarioRequestDTO requestDTO = new BeneficiarioRequestDTO();

        requestDTO.setNome(TestUtils.NOME_BENEF);
        requestDTO.setNumeroTelefone(TestUtils.FONE_BENEF);
        requestDTO.setObservacao(TestUtils.OBS_BENEF);

        return requestDTO;
    }

    public static BeneficiarioResponseDTO criaBeneficiarioResponseDTO(){
        
        BeneficiarioResponseDTO responseDTO = new BeneficiarioResponseDTO();

        responseDTO.setId(Long.valueOf(ID_VALIDO));
        responseDTO.setNome(TestUtils.NOME_BENEF);
        responseDTO.setNumeroTelefone(TestUtils.FONE_BENEF);
        responseDTO.setObservacao(TestUtils.OBS_BENEF);

        return responseDTO;
    }

    public static Emprestimo criarEmprestimo(Beneficiario beneficiario, 
                                       Double valorEmprestimo, 
                                       Double porcentagem,
                                       LocalDateTime dataEmprestimo,
                                       LocalDateTime dataPagamento,
                                       Boolean quitado){

        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setBeneficiario(beneficiario);
        emprestimo.setValorEmprestimo(valorEmprestimo);
        emprestimo.setPorcentagem(porcentagem);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataPagamento(dataPagamento);
        emprestimo.setQuitado(quitado);

        return emprestimo;
    }

    public static EmprestimoRequestDTO criarEmprestimoRequestDTO(Boolean quitado, Beneficiario beneficiario) {
        EmprestimoRequestDTO dto = new EmprestimoRequestDTO();
        dto.setDataEmprestimo("2024-05-01");
        dto.setDataPagamento("2024-06-01");
        dto.setValorEmprestimo(VALOR1000);
        dto.setPorcentagem(PORCENTAGEM20);
        dto.setQuitado(quitado);
        dto.setBeneficiarioId(beneficiario.getId());
        return dto;
    }

    public static EmprestimoResponseDTO criarEmprestimoResponseDTO(long id, boolean quitado, BeneficiarioResponseDTO beneficiario) {
        EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
        dto.setId(id);
        dto.setDataEmprestimo(DATA_EMPRESTIMO1);
        dto.setDataPagamento(DATA_EMPRESTIMO1.plusMonths(1L));
        dto.setValorEmprestimo(VALOR1000);
        dto.setPorcentagem(PORCENTAGEM20);
        dto.setQuitado(quitado);
        dto.setBeneficiario(beneficiario);
        return dto;
    }

    public static Pagamento criarPagamento(Emprestimo emprestimo, Double valorPago, LocalDateTime dataPagamento, TipoPagamento tipoPagamento) {
        Pagamento pagamento = new Pagamento();
        pagamento.setEmprestimo(emprestimo);
        pagamento.setValorPago(valorPago);
        pagamento.setDataPagamento(dataPagamento);
        pagamento.setTipoPagamento(tipoPagamento);
        
        return pagamento;
    }

    public static PagamentoRequestDTO criarPagamentoRequestDTO(Emprestimo emprestimo) {
        PagamentoRequestDTO dto = new PagamentoRequestDTO();

        dto.setDataPagamento("2024-05-01");
        dto.setValorPago(VALOR2000);
        dto.setTipoPagamento(TipoPagamento.TOTAL.toString());
        dto.setEmprestimoId(emprestimo.getId());

        return dto;
    }

    public static PagamentoResponseDTO criarPagamentoResponsetDTO(EmprestimoResponseDTO responseDTO) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();

        dto.setDataPagamento(DATA_EMPRESTIMO2);
        dto.setValorPago(VALOR2000);
        dto.setTipoPagamento(TipoPagamento.TOTAL);
        dto.setEmprestimo(responseDTO);

        return dto;
    }

    public static void criarListaBeneficiario(BeneficiarioRepository repository){
        repository.save(criarBeneficiario());
        repository.save(criarBeneficiario("EDSON MARQUES", "081955554222", "OBS TESTE"));
        repository.save(criarBeneficiario("CAUA MARQUES", "081955554333", "OBS TESTE"));
        repository.save(criarBeneficiario("LAURA ANDRADE", "081955554444", "OBS TESTE"));
    }

    public static void criarListaEmprestimo(EmprestimoRepository repository, Beneficiario beneficiario){
        repository.save(criarEmprestimo(beneficiario, VALOR1000, PORCENTAGEM30, DATA_EMPRESTIMO1, DATA_EMPRESTIMO1.plusMonths(1L), false));
        repository.save(criarEmprestimo(beneficiario, VALOR2000, PORCENTAGEM20, DATA_EMPRESTIMO2, DATA_EMPRESTIMO2.plusMonths(1L), false));
    }
}
