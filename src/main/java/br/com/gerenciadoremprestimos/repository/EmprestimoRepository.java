package br.com.gerenciadoremprestimos.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    /**
     * Encontra empréstimos entre duas datas de empréstimo.
     *
     * @param dataInicial Data inicial
     * @param dataFinal Data final
     * @return Lista de empréstimos
     */
    List<Emprestimo> findByDataEmprestimoBetween(LocalDateTime dataInicial, LocalDateTime dataFinal);

    /**
     * Encontra empréstimos entre duas datas de pagamento.
     *
     * @param dataInicial Data inicial
     * @param dataFinal Data final
     * @return Lista de empréstimos
     */
    List<Emprestimo> findByDataPagamentoBetween(LocalDateTime dataInicial, LocalDateTime dataFinal);

    /**
     * Encontra empréstimos por beneficiário.
     *
     * @param beneficiario Beneficiário
     * @return Lista de empréstimos
     */
    List<Emprestimo> findByBeneficiario(Beneficiario beneficiario);

    /**
     * Encontra empréstimos que não foram quitados antes de uma data específica.
     *
     * @param date Data limite
     * @param quitado Status de quitação
     * @return Lista de empréstimos
     */
    List<Emprestimo> findByDataPagamentoBeforeAndQuitado(LocalDateTime date, boolean quitado);

    /**
     * Encontra empréstimos por status de quitação e ordena por data de pagamento ascendente.
     *
     * @param quitado Status de quitação
     * @return Lista de empréstimos
     */
    List<Emprestimo> findByQuitadoOrderByDataPagamentoAsc(boolean quitado);

    /**
     * Encontra empréstimos pelo mês de pagamento.
     *
     * @param month Mês do pagamento
     * @return Lista de empréstimos
     */
    @Query("SELECT e FROM Emprestimo e WHERE MONTH(e.dataPagamento) = :month")
    List<Emprestimo> findByDataPagamentoMonth(@Param("month") Integer month);

    /**
     * Soma o valor dos empréstimos por mês e ano de pagamento.
     *
     * @param year Ano do pagamento
     * @param month Mês do pagamento
     * @return Soma dos valores dos empréstimos
     */
    @Query("SELECT SUM(e.valorEmprestimo) FROM Emprestimo e WHERE YEAR(e.dataEmprestimo) = :year AND MONTH(e.dataEmprestimo) = :month")
    Double valorTotalEmprestadoPorMes(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * Soma o valor líquido dos empréstimos por mês e ano de pagamento não quitados.
     *
     * @param year Ano do pagamento
     * @param month Mês do pagamento
     * @return Soma dos valores líquidos dos empréstimos
     */
    @Query("SELECT SUM(e.valorEmprestimo + ((e.porcentagem / 100) * e.valorEmprestimo)) FROM Emprestimo e WHERE YEAR(e.dataPagamento) = :year AND MONTH(e.dataPagamento) = :month AND e.quitado = false")
    Double valorTotalLiquidoAReceberPorMes(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * Soma o valor bruto dos empréstimos por mês e ano de pagamento não quitados.
     *
     * @param year Ano do pagamento
     * @param month Mês do pagamento
     * @return Soma dos valores brutos dos empréstimos
     */
    @Query("SELECT SUM((e.porcentagem / 100) * e.valorEmprestimo) FROM Emprestimo e WHERE YEAR(e.dataPagamento) = :year AND MONTH(e.dataPagamento) = :month AND e.quitado = false")
    Double valorTotalBrutoAReceberPorMes(@Param("year") Integer year, @Param("month") Integer month);
}
