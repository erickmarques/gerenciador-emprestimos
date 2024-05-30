package br.com.gerenciadoremprestimos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.EmprestimoUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe de teste para o repositório de Emprestimo.
 */
@DataJpaTest
@ActiveProfiles("test")
public class EmprestimoRepositoryTest {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    public void setUp() {
        Beneficiario beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
        
        beneficiarioRepository.save(beneficiario);

        EmprestimoUtil.criarListaEmprestimo(emprestimoRepository, beneficiario);
    }

    /**
     * Testa a soma de valor emprestado durante o mês.
     * Deve retornar o valor de 30000,00.
     */
    @Test
    @DisplayName("Teste de soma de valor total emprestado no mês.")
    public void valorTotalEmprestadoPorMes_RetornaValorCorreto() {
        Double totalEmprestado = emprestimoRepository.valorTotalEmprestadoPorMes(TestUtils.ANO, TestUtils.MES);
        assertNotNull(totalEmprestado, "O valor total emprestado não deve ser nulo");
        assertEquals(TestUtils.VALOR3000, totalEmprestado, "O valor total emprestado deve ser 3000");
    }


    /**
     * Testa a soma de valor emprestado durante o mês.
     * Deve retornar o valor de 30000,00 e estou comparando com o valor -9999,00.
     */
    @Test
    @DisplayName("Teste de soma com valor errado do total emprestado no mês.")
    public void valorTotalEmprestadoPorMes_RetornaValorInexistente() {
        Double totalEmprestado = emprestimoRepository.valorTotalEmprestadoPorMes(TestUtils.ANO, TestUtils.MES);
        assertNotNull(totalEmprestado, "O valor total emprestado não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalEmprestado, "O valor total emprestado não deve ser -9999");
    }


    /**
     * Deve retornar o valor nulo.
     */
    @Test
    @DisplayName("Teste de soma com valor nulo do total emprestado no mês.")
    public void valorTotalEmprestadoPorMes_RetornaNulo() {
        Double totalEmprestado = emprestimoRepository.valorTotalEmprestadoPorMes(TestUtils.ANO, TestUtils.MES - 1);
        assertNull(totalEmprestado, "O valor total emprestado deve ser nulo para meses sem empréstimos");
    }

    /**
     * Testa a soma do valor total líquido a receber no mês.
     * Deve retornar o valor de 700,00.
     */
    @Test
    @DisplayName("Teste de soma de valor total líquido a receber no mês.")
    public void valorTotalLiquidoAReceberPorMes_RetornaValorCorreto() {
        Double totalLiquido = emprestimoRepository.valorTotalLiquidoAReceberPorMes(TestUtils.ANO, TestUtils.MES + 1);
        Double expectedTotalLiquido = TestUtils.VALOR1000 + (EmprestimoUtil.PORCENTAGEM30 / 100 * TestUtils.VALOR1000) + TestUtils.VALOR2000 + (EmprestimoUtil.PORCENTAGEM20 / 100 * TestUtils.VALOR2000);
        
        assertNotNull(totalLiquido, "O valor total líquido a receber não deve ser nulo");
        assertEquals(expectedTotalLiquido, totalLiquido, "O valor total líquido a receber deve ser o esperado");
    }

    /**
     * Testa a soma do valor total líquido a receber no mês com um valor inexistente.
     * Deve retornar o valor de 700,00 e está sendo comparado com o valor de -9999,00.
     */
    @Test
    @DisplayName("Teste de soma de valor total líquido a receber no mês com um valor inexistente.")
    public void valorTotalLiquidoAReceberPorMes_RetornaValorInexistente() {
        Double totalLiquido = emprestimoRepository.valorTotalLiquidoAReceberPorMes(TestUtils.ANO, TestUtils.MES + 1);
        assertNotNull(totalLiquido, "O valor total líquido a receber não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalLiquido, "O valor total líquido a receber não deve ser -9999");
    }

    /**
     *  Deve retornar o valor nulo.
     */
    @Test
    @DisplayName("Teste de soma de valor total líquido a receber no mês com um valor nulo.")
    public void valorTotalLiquidoAReceberPorMes_RetornaNulo() {
        Double totalLiquido = emprestimoRepository.valorTotalLiquidoAReceberPorMes(TestUtils.ANO, TestUtils.MES - 1);
        assertNull(totalLiquido, "O valor total líquido a receber deve ser nulo para meses sem empréstimos");
    }

    
    /**
     * Testa a soma do valor total bruto a receber no mês.
     * Deve retornar o valor de 3.700,00
     */
    @Test
    @DisplayName("Teste de soma de valor total bruto a receber no mês.")
    public void valorTotalBrutoAReceberPorMes_RetornaValorCorreto() {
        Double totalBruto = emprestimoRepository.valorTotalBrutoAReceberPorMes(TestUtils.ANO, TestUtils.MES + 1);
        assertNotNull(totalBruto, "O valor total bruto a receber não deve ser nulo");
        Double expectedTotalBruto = (EmprestimoUtil.PORCENTAGEM30 / 100 * TestUtils.VALOR1000) + (EmprestimoUtil.PORCENTAGEM20 / 100 * TestUtils.VALOR2000);
        assertEquals(expectedTotalBruto, totalBruto, "O valor total bruto a receber deve ser o esperado");
    }

    /**
     * Testa a soma do valor total bruto a receber no mês com um valor inexistente.
     * Deve retornar o valor de 3.700,00 e está sendo comparado com o valor de -9999,00
     */
    @Test
    @DisplayName("Teste de soma de valor total bruto a receber no mês com um valor inexistente.")
    public void valorTotalBrutoAReceberPorMes_RetornaValorInexistente() {
        Double totalBruto = emprestimoRepository.valorTotalBrutoAReceberPorMes(TestUtils.ANO, TestUtils.MES + 1);
        assertNotNull(totalBruto, "O valor total bruto a receber não deve ser nulo");
        assertNotEquals(TestUtils.VALOR_INEXISTENTE, totalBruto, "O valor total bruto a receber não deve ser -9999");
    }

    /**
     * Deve retornar o valor nulo.
     */
    @Test
    @DisplayName("Teste de soma de valor total bruto a receber no mês com o valor nulo")
    public void valorTotalBrutoAReceberPorMes_RetornaNulo() {
        Double totalBruto = emprestimoRepository.valorTotalBrutoAReceberPorMes(TestUtils.ANO, TestUtils.MES - 1);
        assertNull(totalBruto, "O valor total bruto a receber deve ser nulo para meses sem empréstimos");
    }
}
