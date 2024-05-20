package br.com.gerenciadoremprestimos.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um DTO de requisiçao (GET) da entidade Beneficiário.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarioResponseDTO {
    private Long id;
    private String nome;
    private String numeroTelefone;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String observacao;
}
