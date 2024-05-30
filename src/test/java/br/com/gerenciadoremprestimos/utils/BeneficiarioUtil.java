package br.com.gerenciadoremprestimos.utils;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;

public class BeneficiarioUtil {

    public static final String NOME_BENEF               = "Erick Marques";
    public static final String FONE_BENEF               = "081988888888";
    public static final String OBS_BENEF                = "Observação de teste";
    
    public static void criarListaBeneficiario(BeneficiarioRepository repository){
        repository.save(criarBeneficiarioPadrao());
        repository.save(criarBeneficiario("EDSON MARQUES", "081955554222", "OBS TESTE"));
        repository.save(criarBeneficiario("CAUA MARQUES", "081955554333", "OBS TESTE"));
        repository.save(criarBeneficiario("LAURA ANDRADE", "081955554444", "OBS TESTE"));
    }

    public static Beneficiario criarBeneficiarioPadrao(){
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome(NOME_BENEF);
        beneficiario.setNumeroTelefone(FONE_BENEF);
        beneficiario.setObservacao(OBS_BENEF);

        return beneficiario;
    }

    public static Beneficiario criarBeneficiario(String nome, String telefone, String observacao){
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome(nome);
        beneficiario.setNumeroTelefone(telefone);
        beneficiario.setObservacao(observacao);
        return beneficiario;
    }

        public static BeneficiarioRequestDTO criaBeneficiarioRequestDTO(){
        
        BeneficiarioRequestDTO requestDTO = new BeneficiarioRequestDTO();

        requestDTO.setNome(NOME_BENEF);
        requestDTO.setNumeroTelefone(FONE_BENEF);
        requestDTO.setObservacao(OBS_BENEF);

        return requestDTO;
    }

    public static BeneficiarioResponseDTO criaBeneficiarioResponseDTO(){
        
        BeneficiarioResponseDTO responseDTO = new BeneficiarioResponseDTO();

        responseDTO.setId(Long.valueOf(TestUtils.ID_VALIDO));
        responseDTO.setNome(NOME_BENEF);
        responseDTO.setNumeroTelefone(FONE_BENEF);
        responseDTO.setObservacao(OBS_BENEF);

        return responseDTO;
    }
}
