package br.com.gerenciadoremprestimos.dto;

import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um DTO de requisiçao (insert/update) da entidade Pagamento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoRequestDTO {
    
    @NotNull(message = "{pagamento.dataPagamento.naoNulo}")
    private String dataPagamento;

    @Positive(message = "{pagamento.valorPago.positive}")
    @NotNull(message = "{pagamento.valorPago.naoNulo}")
    private Double valorPago;

    @NotNull(message = "{pagamento.tipoPagamento.naoNulo}")
    @Pattern(regexp = "TOTAL|JUROS|ACORDO", message = "{pagamento.tipoPagamento.invalido}")
    private String tipoPagamento;

    @NotNull(message = "{pagamento.emprestimo.naoNulo}")
    private Long emprestimoId;
}
