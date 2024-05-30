package br.com.gerenciadoremprestimos.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;


/**
 * Classe de teste para o repositório de Beneficiario.
 */
@DataJpaTest
@ActiveProfiles("test")
public class BeneficiarioRepositoryTest {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    private final String NOME_PESQUISA = "MARQUES";
    private final String NOME_LAURA    = "LAURA";

    /**
     * Configura o ambiente de teste antes de cada teste.
     */
    @BeforeEach
    public void setUp() {
        BeneficiarioUtil.criarListaBeneficiario(beneficiarioRepository);
    }

    /**
     * Testa a pesquisa por nome contendo 'MARQUES' em maiúsculas.
     * Deve retornar 3 beneficiários.
     */
    @Test
    @DisplayName("Teste de pesquisa parcialmente pelo nome do beneficiário com letra maiúscula, devendo retornar 3 beneficiários")
    public void findByNomeContainingIgnoreCase_ComLetraMaiuscula_DeveRetornarTresBeneficiario(){

        List<Beneficiario> beneficiarios = beneficiarioRepository.findByNomeContainingIgnoreCase(NOME_PESQUISA);

        validarBeneficiarios(beneficiarios, 3);
    }

    /**
     * Testa a pesquisa por nome contendo 'marques' em minúsculas.
     * Deve retornar 3 beneficiários.
     */
    @Test
    @DisplayName("Teste de pesquisa parcialmente pelo nome do beneficiário com letra minúscula, devendo retornar 3 beneficiários")
    public void findByNomeContainingIgnoreCase_ComLetraMinuscula_DeveRetornarTresBeneficiario(){

        List<Beneficiario> beneficiarios = beneficiarioRepository.findByNomeContainingIgnoreCase(NOME_PESQUISA.toLowerCase());

        validarBeneficiarios(beneficiarios, 3);
    }

    /**
     * Testa a pesquisa por nome contendo 'LAURA'.
     * Deve retornar 1 beneficiário.
     */
    @Test
    @DisplayName("Teste de pesquisa parcialmente pelo nome do beneficiário, devendo retornar 1 beneficiário")
    public void findByNomeContainingIgnoreCase_DeveRetornarUmBeneficiario(){

        List<Beneficiario> beneficiarios = beneficiarioRepository.findByNomeContainingIgnoreCase(NOME_LAURA);

        validarBeneficiarios(beneficiarios, 1);
    }

    /**
     * Valida a lista de beneficiários retornada pela pesquisa.
     * @param beneficiarios a lista de beneficiários
     * @param tamanhoEsperado o tamanho esperado da lista
     */
    private void validarBeneficiarios(List<Beneficiario> beneficiarios, int tamanhoEsperado) {
        assertNotNull(beneficiarios, "A lista de beneficiários não deve ser nula");
        assertFalse(beneficiarios.isEmpty(), "A lista de beneficiários não deve estar vazia");
        assertEquals(tamanhoEsperado, beneficiarios.size(), "A lista de beneficiários deve conter " + tamanhoEsperado + " elementos");
    }
    
}
