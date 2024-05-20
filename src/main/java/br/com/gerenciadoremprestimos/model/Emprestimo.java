package br.com.gerenciadoremprestimos.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidade que representa um empréstimo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "emprestimo", schema = "public")
public class Emprestimo extends Base {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{emprestimo.dataEmprestimo.naoNulo}")
    @Column(name = "data_emprestimo", nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @NotNull(message = "{emprestimo.valorEmprestimo.naoNulo}")
    @Positive(message = "{emprestimo.valorEmprestimo.positive}")
    @Column(name = "valor_emprestimo", nullable = false)
    private Double valorEmprestimo;

    @NotNull(message = "{emprestimo.porcentagem.naoNulo}")
    @PositiveOrZero(message = "{emprestimo.porcentagem.positivoOuZero}")
    @Column(name = "porcentagem", nullable = false)
    private Double porcentagem;

    @NotNull(message = "{emprestimo.quitado.naoNulo}")
    @Column(name = "quitado", nullable = false)
    private Boolean quitado;

    @ManyToOne
    @JoinColumn(name = "beneficiario_id", nullable = false)
    @NotNull(message = "{emprestimo.beneficiario.naoNulo}")
    private Beneficiario beneficiario;
}
