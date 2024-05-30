package br.com.gerenciadoremprestimos.utils;

import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import java.time.LocalDateTime;

public class EmprestimoUtil {

    public static final Double PORCENTAGEM30            = 30.0;
    public static final Double PORCENTAGEM20            = 20.0;
    public static final LocalDateTime DATA_EMPRESTIMO1  = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 1, 0, 0);
    public static final LocalDateTime DATA_EMPRESTIMO2  = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 20, 0, 0);
    
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
        dto.setValorEmprestimo(TestUtils.VALOR1000);
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
        dto.setValorEmprestimo(TestUtils.VALOR1000);
        dto.setPorcentagem(PORCENTAGEM20);
        dto.setQuitado(quitado);
        dto.setBeneficiario(beneficiario);
        return dto;
    }
        public static void criarListaEmprestimo(EmprestimoRepository repository, Beneficiario beneficiario){
        repository.save(criarEmprestimo(beneficiario, TestUtils.VALOR1000, PORCENTAGEM30, DATA_EMPRESTIMO1, DATA_EMPRESTIMO1.plusMonths(1L), false));
        repository.save(criarEmprestimo(beneficiario, TestUtils.VALOR2000, PORCENTAGEM20, DATA_EMPRESTIMO2, DATA_EMPRESTIMO2.plusMonths(1L), false));
    }
}
