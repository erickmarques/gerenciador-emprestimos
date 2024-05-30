package br.com.gerenciadoremprestimos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.EmprestimoUtil;
import br.com.gerenciadoremprestimos.utils.PagamentoUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe de teste para o repositório de Pagamento.
 */
@DataJpaTest
@ActiveProfiles("test")
public class PagamentoRepositoryTest {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    private Emprestimo emprestimo;


    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    public void setUp() {
        Beneficiario beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
        
        beneficiarioRepository.save(beneficiario);

        emprestimo = EmprestimoUtil.criarEmprestimo(beneficiario, TestUtils.VALOR3000, EmprestimoUtil.PORCENTAGEM20, EmprestimoUtil.DATA_EMPRESTIMO1, EmprestimoUtil.DATA_EMPRESTIMO1.plusMonths(1), false);
        emprestimoRepository.save(emprestimo);

        PagamentoUtil.criarListaPagamento(pagamentoRepository, emprestimo);
    }

    
    /**
     * Testa a soma do valor recebido de um empréstimo.
     * Deve retornar o valor de 30000,00.
     */
    @Test
    @DisplayName("Testa a soma do valor recebido um empréstimo.")
    public void valorTotalRecebidoPorEmprestimo_RetornaValorCorreto() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorEmprestimo(emprestimo);
        assertNotNull(totalRecebido, "O valor total recebido por empréstimo não deve ser nulo");
        assertEquals(TestUtils.VALOR3000, totalRecebido, "O valor total recebido por empréstimo deve ser 3000");
    }

    /**
     * Testa a soma com valor errado recebido de um empréstimo.
     * Deve retornar o valor de 30000,00 e está sendo comparado com o valor de -9999,00.
     */
    @Test
    @DisplayName("Testa a soma com valor errado recebido de um empréstimo.")
    public void valorTotalRecebidoPorEmprestimo_RetornaValorInexistente() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorEmprestimo(emprestimo);
        assertNotNull(totalRecebido, "O valor total recebido por empréstimo não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalRecebido, "O valor total recebido por empréstimo não deve ser -9999");
    }

    /**
     *  Deve retornar o valor nulo.
     */
    @Test
    @DisplayName("Testa a soma com valor nulo recebido de um empréstimo.")
    public void valorTotalRecebidoPorEmprestimo_RetornaNulo() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorEmprestimo(null);
        assertNull(totalRecebido, "O valor total recebido deve ser nulo para empréstimo errado");
    }

     /**
     * Testa a soma do valor recebido por mês.
     * Deve retornar o valor de 30000,00.
     */
    @Test
    @DisplayName("Testa a soma do valor recebido por mês.")
    public void valorTotalRecebidoPorMes_RetornaValorCorreto() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorMes(TestUtils.ANO, TestUtils.MES);
        assertNotNull(totalRecebido, "O valor total recebido por mês não deve ser nulo");
        assertEquals(TestUtils.VALOR3000, totalRecebido, "O valor total recebido por mês deve ser 3000");
    }

    /**
     * Testa a soma do valor errado recebido por mês.
     * Deve retornar o valor de 30000,00 e está sendo comparado com o valor de -9999,00.
     */
    @Test
    @DisplayName("Testa a soma com valor errado do valor recebido por mês.")
    public void valorTotalRecebidoPorMes_RetornaValorInexistente() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorMes(TestUtils.ANO, TestUtils.MES);
        assertNotNull(totalRecebido, "O valor total recebido por mês não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalRecebido, "O valor total recebido por mês não deve ser -9999");
    }

    /**
     *  Deve retornar o valor nulo.
     */
    @Test
    @DisplayName("Testa a soma com valor nulo do valor recebido por mês.")
    public void valorTotalRecebidoPorMes_RetornaNulo() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorMes(TestUtils.ANO, TestUtils.MES - 1);
        assertNull(totalRecebido, "O valor total recebido deve ser nulo para meses sem pagamentos");
    }
}
