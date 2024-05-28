package br.com.gerenciadoremprestimos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;

/**
 * Representa um DTO de retorno da entidade Pagamento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoResponseDTO {
    private Long id;
    private LocalDateTime dataPagamento;
    private Double valorPago;
    private TipoPagamento tipoPagamento;
    private EmprestimoResponseDTO emprestimo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String observacao;
}
