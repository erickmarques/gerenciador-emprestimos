package br.com.gerenciadoremprestimos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gerenciadoremprestimos.model.Beneficiario;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
    
    /**
     * Pesquisar por nome parcialmente e ignorando letra maisculas e minusculas de beneficiários 
     *
     * @param nome Nome do beneficiário
     * @return Lista de Beneficiários
     */
    List<Beneficiario> findByNomeContainingIgnoreCase(String nome);
}