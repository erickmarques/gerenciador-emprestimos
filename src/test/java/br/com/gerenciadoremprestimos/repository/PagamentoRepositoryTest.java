package br.com.gerenciadoremprestimos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import br.com.gerenciadoremprestimos.TestUtils;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class PagamentoRepositoryTest {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    private Emprestimo emprestimo;

    @BeforeEach
    public void setUp() {
        Beneficiario beneficiario = TestUtils.criarBeneficiario();
        beneficiarioRepository.save(beneficiario);

        LocalDateTime dataEmprestimo = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 1, 0, 0);
        emprestimo = TestUtils.criarEmprestimo(beneficiario, TestUtils.VALOR3000, TestUtils.PORCENTAGEM20, dataEmprestimo, dataEmprestimo.plusMonths(1), false);
        emprestimoRepository.save(emprestimo);

        LocalDateTime dataPagamento1 = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 10, 0, 0);
        LocalDateTime dataPagamento2 = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 20, 0, 0);

        Pagamento pagamento1 = TestUtils.criarPagamento(emprestimo, TestUtils.VALOR1000, dataPagamento1, TipoPagamento.JUROS);
        Pagamento pagamento2 = TestUtils.criarPagamento(emprestimo, TestUtils.VALOR2000, dataPagamento2, TipoPagamento.TOTAL);

        pagamentoRepository.save(pagamento1);
        pagamentoRepository.save(pagamento2);
    }

    @Test
    public void valorTotalRecebidoPorEmprestimo_RetornaValorCorreto() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorEmprestimo(emprestimo);
        assertNotNull(totalRecebido, "O valor total recebido por empréstimo não deve ser nulo");
        assertEquals(TestUtils.VALOR3000, totalRecebido, "O valor total recebido por empréstimo deve ser 3000");
    }

    @Test
    public void valorTotalRecebidoPorEmprestimo_RetornaValorInexistente() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorEmprestimo(emprestimo);
        assertNotNull(totalRecebido, "O valor total recebido por empréstimo não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalRecebido, "O valor total recebido por empréstimo não deve ser -9999");
    }

    @Test
    public void valorTotalRecebidoPorEmprestimo_RetornaNulo() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorEmprestimo(null);
        assertNull(totalRecebido, "O valor total recebido deve ser nulo para empréstimo inexistente");
    }

    @Test
    public void valorTotalRecebidoPorMes_RetornaValorCorreto() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorMes(TestUtils.ANO, TestUtils.MES);
        assertNotNull(totalRecebido, "O valor total recebido por mês não deve ser nulo");
        assertEquals(TestUtils.VALOR3000, totalRecebido, "O valor total recebido por mês deve ser 3000");
    }

    @Test
    public void valorTotalRecebidoPorMes_RetornaValorInexistente() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorMes(TestUtils.ANO, TestUtils.MES);
        assertNotNull(totalRecebido, "O valor total recebido por mês não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalRecebido, "O valor total recebido por mês não deve ser -9999");
    }

    @Test
    public void valorTotalRecebidoPorMes_RetornaNulo() {
        Double totalRecebido = pagamentoRepository.valorTotalRecebidoPorMes(TestUtils.ANO, TestUtils.MES - 1);
        assertNull(totalRecebido, "O valor total recebido deve ser nulo para meses sem pagamentos");
    }
}
