package br.com.gerenciadoremprestimos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidade representando um beneficiário.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "beneficiario", schema = "public")
public class Beneficiario extends Base {

    private static final long serialVersionUID = 1L;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "numero_telefone", nullable = false)
    private String numeroTelefone;
}
