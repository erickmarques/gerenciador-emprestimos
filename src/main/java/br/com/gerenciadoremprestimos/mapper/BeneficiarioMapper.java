package br.com.gerenciadoremprestimos.mapper;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import org.springframework.stereotype.Component;

@Component
public class BeneficiarioMapper {

    public Beneficiario paraEntidade(BeneficiarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Beneficiario beneficiario = new Beneficiario();
        
        return atualizarCampos(beneficiario, dto);
    }

    public Beneficiario paraEntidadeAtualizar(Beneficiario beneficiario, BeneficiarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return atualizarCampos(beneficiario, dto);
    }

    private Beneficiario atualizarCampos(Beneficiario beneficiario, BeneficiarioRequestDTO dto){
        beneficiario.setNome(dto.getNome());
        beneficiario.setNumeroTelefone(dto.getNumeroTelefone());
        beneficiario.setObservacao(dto.getObservacao());

        return beneficiario;
    }

    public BeneficiarioResponseDTO paraDto(Beneficiario beneficiario) {
        if (beneficiario == null) {
            return null;
        }
        BeneficiarioResponseDTO dto = new BeneficiarioResponseDTO();

        dto.setId(beneficiario.getId());
        dto.setNome(beneficiario.getNome());
        dto.setNumeroTelefone(beneficiario.getNumeroTelefone());
        dto.setDataCriacao(beneficiario.getDataCriacao());
        dto.setDataAtualizacao(beneficiario.getDataAtualizacao());
        dto.setObservacao(beneficiario.getObservacao());

        return dto;
    }
}
