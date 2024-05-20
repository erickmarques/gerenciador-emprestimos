package br.com.gerenciadoremprestimos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um DTO de requisiçao (GET) da entidade Empréstimo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprestimoResponseDTO {

    @NotEmpty(message = "{emprestimo.dataEmprestimo.naoNulo}")
    private String dataEmprestimo;

    @NotEmpty(message = "{emprestimo.dataEmprestimo.naoNulo}")
    private String dataPagamento;

    @Positive(message = "{emprestimo.valorEmprestimo.positive}")
    @NotNull(message = "{emprestimo.valorEmprestimo.naoNulo}")
    private Double valorEmprestimo;

    @NotNull(message = "{emprestimo.porcentagem.naoNulo}")
    @PositiveOrZero(message = "{emprestimo.porcentagem.positivoOuZero}")
    private Double porcentagem;

    @NotNull(message = "{emprestimo.quitado.naoNulo}")
    private Boolean quitado;

    @NotNull(message = "{emprestimo.beneficiario.naoNulo}")
    private Long beneficiario;
}
