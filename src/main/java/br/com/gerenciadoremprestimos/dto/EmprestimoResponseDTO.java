package br.com.gerenciadoremprestimos.dto;

import java.time.LocalDateTime;

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

    private Long id;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataPagamento;
    private Double valorEmprestimo;
    private Double porcentagem;
    private Boolean quitado;
    private BeneficiarioResponseDTO beneficiario;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String observacao;
}
