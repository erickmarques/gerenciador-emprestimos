package br.com.gerenciadoremprestimos.utils;

import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoResponseDTO;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.repository.PagamentoRepository;
import java.time.LocalDateTime;

public class PagamentoUtil {

    public static final LocalDateTime DATA_PAGAMENTO1   = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 10, 0, 0);
    public static final LocalDateTime DATA_PAGAMENTO2   = LocalDateTime.of(TestUtils.ANO, TestUtils.MES, 20, 0, 0);
    
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
        dto.setValorPago(TestUtils.VALOR2000);
        dto.setTipoPagamento(TipoPagamento.TOTAL.toString());
        dto.setEmprestimoId(emprestimo.getId());

        return dto;
    }

    public static PagamentoResponseDTO criarPagamentoResponsetDTO(EmprestimoResponseDTO responseDTO) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();

        dto.setDataPagamento(DATA_PAGAMENTO1);
        dto.setValorPago(TestUtils.VALOR2000);
        dto.setTipoPagamento(TipoPagamento.TOTAL);
        dto.setEmprestimo(responseDTO);

        return dto;
    }


    public static void criarListaPagamento(PagamentoRepository repository, Emprestimo emprestimo){
        repository.save(criarPagamento(emprestimo, TestUtils.VALOR1000, DATA_PAGAMENTO1, TipoPagamento.JUROS));
        repository.save(criarPagamento(emprestimo, TestUtils.VALOR2000, DATA_PAGAMENTO2, TipoPagamento.TOTAL));
    }
}
