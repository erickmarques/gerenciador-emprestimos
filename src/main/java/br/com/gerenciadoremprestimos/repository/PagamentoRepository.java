package br.com.gerenciadoremprestimos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    /**
     * Encontra pagamentos para um empréstimo ordenados pela data de pagamento ascendente.
     *
     * @param Emprestimo Empréstimo
     * @return Lista de pagamentos
     */
    List<Pagamento> findByEmprestimoOrderByDataPagamentoAsc(Emprestimo emprestimo);

    /**
     * Soma o valor total dos pagamentos para um empréstimo.
     *
     * @param Emprestimo Empréstimo
     * @return Valor total dos pagamentos
     */
    @Query("SELECT SUM(p.valorPago) FROM Pagamento p WHERE p.emprestimo = :emprestimo")
    Double valorTotalRecebidoPorEmprestimo(Emprestimo emprestimo);

    /**
     * Soma o valor total dos pagamentos por ano e mês.
     *
     * @param ano Ano
     * @param mes Mês
     * @return Valor total dos pagamentos
     */
    @Query("SELECT SUM(p.valorPago) FROM Pagamento p WHERE YEAR(p.dataPagamento) = :ano AND MONTH(p.dataPagamento) = :mes")
    Double valorTotalRecebidoPorMes(@Param("ano") int ano, @Param("mes") int mes);


}                    

