package br.com.gerenciadoremprestimos;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;

import java.time.LocalDateTime;

public class Utils {

    public static final int ANO                   = 2024;
    public static final int MES                   = 5;
    public static final Double PORCENTAGEM30      = 30.0;
    public static final Double PORCENTAGEM20      = 20.0;
    public static final Double VALOR1000          = 1000.0;
    public static final Double VALOR2000          = 2000.0;
    public static final Double VALOR3000          = 3000.0;
    public static final Double VALOR_INEXISTENTE  = -9999.0;
    
    public static final String ID_BENEF           = "1";
    public static final String NOME_BENEF         = "Erick Marques";
    public static final String FONE_BENEF         = "081988888888";
    public static final String OBS_BENEF          = "Observação de teste";

    public static final String ID_INVALIDO        = "abc";
    public static final Long ID_INEXISTENTE       = 99999L;
 
    public static Beneficiario criarBeneficiario(){
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome(NOME_BENEF);
        beneficiario.setNumeroTelefone(FONE_BENEF);
        beneficiario.setObservacao(OBS_BENEF);

        return beneficiario;
    }

    public static BeneficiarioRequestDTO criaBeneficiarioRequestDTO(){
        
        BeneficiarioRequestDTO requestDTO = new BeneficiarioRequestDTO();

        requestDTO.setNome(Utils.NOME_BENEF);
        requestDTO.setNumeroTelefone(Utils.FONE_BENEF);
        requestDTO.setObservacao(Utils.OBS_BENEF);

        return requestDTO;
    }

    public static BeneficiarioResponseDTO criaBeneficiarioResponseDTO(){
        
        BeneficiarioResponseDTO responseDTO = new BeneficiarioResponseDTO();

        responseDTO.setId(Long.valueOf(ID_BENEF));
        responseDTO.setNome(Utils.NOME_BENEF);
        responseDTO.setNumeroTelefone(Utils.FONE_BENEF);
        responseDTO.setObservacao(Utils.OBS_BENEF);

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

    
    public static Pagamento criarPagamento(Emprestimo emprestimo, Double valorPago, LocalDateTime dataPagamento, TipoPagamento tipoPagamento) {
        Pagamento pagamento = new Pagamento();
        pagamento.setEmprestimo(emprestimo);
        pagamento.setValorPago(valorPago);
        pagamento.setDataPagamento(dataPagamento);
        pagamento.setTipoPagamento(tipoPagamento);
        
        return pagamento;
    }
}
