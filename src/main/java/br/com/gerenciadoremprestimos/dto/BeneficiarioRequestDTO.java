package br.com.gerenciadoremprestimos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um DTO de requisiçao (insert/update) da entidade Beneficiário.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarioRequestDTO {

    @NotBlank(message = "{beneficiario.nome.naoVazio}")
    @Size(max = 100, message = "{beneficiario.nome.tamanho}")
    private String nome;

    @NotBlank(message = "{beneficiario.numeroTelefone.naoVazio}")
    @Pattern(regexp = "\\d{10,15}", message = "{beneficiario.numeroTelefone.pattern}")
    private String numeroTelefone;
    private String observacao;
}
