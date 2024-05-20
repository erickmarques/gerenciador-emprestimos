package br.com.gerenciadoremprestimos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import br.com.gerenciadoremprestimos.Utils;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class EmprestimoRepositoryTest {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @BeforeEach
    public void setUp() {
        Beneficiario beneficiario = Utils.criarBeneficiario();
        beneficiarioRepository.save(beneficiario);

        LocalDateTime dataEmprestimo1 = LocalDateTime.of(Utils.ANO, Utils.MES, 15, 0, 0);
        LocalDateTime dataEmprestimo2 = LocalDateTime.of(Utils.ANO, Utils.MES, 20, 0, 0);

        Emprestimo emprestimo1 = Utils.criarEmprestimo(beneficiario, Utils.VALOR1000, Utils.PORCENTAGEM30, dataEmprestimo1, dataEmprestimo1.plusMonths(1L), false);
        Emprestimo emprestimo2 = Utils.criarEmprestimo(beneficiario, Utils.VALOR2000, Utils.PORCENTAGEM20, dataEmprestimo2, dataEmprestimo2.plusMonths(1L), false);

        emprestimoRepository.save(emprestimo1);
        emprestimoRepository.save(emprestimo2);
    }

    @Test
    public void valorTotalEmprestadoPorMes_RetornaValorCorreto() {
        Double totalEmprestado = emprestimoRepository.valorTotalEmprestadoPorMes(Utils.ANO, Utils.MES);
        assertNotNull(totalEmprestado, "O valor total emprestado não deve ser nulo");
        assertEquals(Utils.VALOR3000, totalEmprestado, "O valor total emprestado deve ser 3000");
    }

    @Test
    public void valorTotalEmprestadoPorMes_RetornaValorInexistente() {
        Double totalEmprestado = emprestimoRepository.valorTotalEmprestadoPorMes(Utils.ANO, Utils.MES);
        assertNotNull(totalEmprestado, "O valor total emprestado não deve ser nulo");
        assertNotEquals(Utils.VALOR_INEXISTENTE, totalEmprestado, "O valor total emprestado não deve ser -9999");
    }

    @Test
    public void valorTotalEmprestadoPorMes_RetornaNulo() {
        Double totalEmprestado = emprestimoRepository.valorTotalEmprestadoPorMes(Utils.ANO, Utils.MES - 1);
        assertNull(totalEmprestado, "O valor total emprestado deve ser nulo para meses sem empréstimos");
    }

    @Test
    public void valorTotalLiquidoAReceberPorMes_RetornaValorCorreto() {
        Double totalLiquido = emprestimoRepository.valorTotalLiquidoAReceberPorMes(Utils.ANO, Utils.MES + 1);
        assertNotNull(totalLiquido, "O valor total líquido a receber não deve ser nulo");
        Double expectedTotalLiquido = Utils.VALOR1000 + (Utils.PORCENTAGEM30 / 100 * Utils.VALOR1000) + Utils.VALOR2000 + (Utils.PORCENTAGEM20 / 100 * Utils.VALOR2000);
        assertEquals(expectedTotalLiquido, totalLiquido, "O valor total líquido a receber deve ser o esperado");
    }

    @Test
    public void valorTotalLiquidoAReceberPorMes_RetornaValorInexistente() {
        Double totalLiquido = emprestimoRepository.valorTotalLiquidoAReceberPorMes(Utils.ANO, Utils.MES + 1);
        assertNotNull(totalLiquido, "O valor total líquido a receber não deve ser nulo");
        assertNotEquals(Utils.VALOR_INEXISTENTE, totalLiquido, "O valor total líquido a receber não deve ser -9999");
    }

    @Test
    public void valorTotalLiquidoAReceberPorMes_RetornaNulo() {
        Double totalLiquido = emprestimoRepository.valorTotalLiquidoAReceberPorMes(Utils.ANO, Utils.MES - 1);
        assertNull(totalLiquido, "O valor total líquido a receber deve ser nulo para meses sem empréstimos");
    }

    @Test
    public void valorTotalBrutoAReceberPorMes_RetornaValorCorreto() {
        Double totalBruto = emprestimoRepository.valorTotalBrutoAReceberPorMes(Utils.ANO, Utils.MES + 1);
        assertNotNull(totalBruto, "O valor total bruto a receber não deve ser nulo");
        Double expectedTotalBruto = (Utils.PORCENTAGEM30 / 100 * Utils.VALOR1000) + (Utils.PORCENTAGEM20 / 100 * Utils.VALOR2000);
        assertEquals(expectedTotalBruto, totalBruto, "O valor total bruto a receber deve ser o esperado");
    }

    @Test
    public void valorTotalBrutoAReceberPorMes_RetornaValorInexistente() {
        Double totalBruto = emprestimoRepository.valorTotalBrutoAReceberPorMes(Utils.ANO, Utils.MES + 1);
        assertNotNull(totalBruto, "O valor total bruto a receber não deve ser nulo");
        assertNotEquals(Utils.VALOR_INEXISTENTE, totalBruto, "O valor total bruto a receber não deve ser -9999");
    }

    @Test
    public void valorTotalBrutoAReceberPorMes_RetornaNulo() {
        Double totalBruto = emprestimoRepository.valorTotalBrutoAReceberPorMes(Utils.ANO, Utils.MES - 1);
        assertNull(totalBruto, "O valor total bruto a receber deve ser nulo para meses sem empréstimos");
    }
}
