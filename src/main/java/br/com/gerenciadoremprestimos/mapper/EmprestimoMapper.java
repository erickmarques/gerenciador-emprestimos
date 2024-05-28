package br.com.gerenciadoremprestimos.mapper;

import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.util.Utils;
import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class EmprestimoMapper {

    private final MessageSource messageSource;
    private final BeneficiarioMapper beneficiarioMapper;

    public Emprestimo paraEntidade(EmprestimoRequestDTO dto, Beneficiario beneficiario) {
        if (dto == null) {
            return null;
        }
        
        Emprestimo emprestimo = new Emprestimo();
        
        return atualizarCampos(emprestimo, dto, beneficiario);
    }

    public Emprestimo paraEntidadeAtualizar(Emprestimo emprestimo, EmprestimoRequestDTO dto, Beneficiario beneficiario) {
        if (dto == null) {
            return null;
        }

        return atualizarCampos(emprestimo, dto, beneficiario);
    }

    private Emprestimo atualizarCampos(Emprestimo emprestimo, EmprestimoRequestDTO dto, Beneficiario beneficiario){
        try {
            emprestimo.setDataEmprestimo(Utils.convertStringToLocalDateTime(dto.getDataEmprestimo()));
            emprestimo.setDataPagamento(Utils.convertStringToLocalDateTime(dto.getDataPagamento()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("data.invalida"));
        }

        emprestimo.setValorEmprestimo(dto.getValorEmprestimo());
        emprestimo.setPorcentagem(dto.getPorcentagem());
        emprestimo.setQuitado(dto.getQuitado());
        emprestimo.setBeneficiario(beneficiario);

        return emprestimo;
    }

    public EmprestimoResponseDTO paraDto(Emprestimo emprestimo) {
        if (emprestimo == null) {
            return null;
        }
        EmprestimoResponseDTO dto = new EmprestimoResponseDTO();

        dto.setId(emprestimo.getId());
        dto.setDataEmprestimo(emprestimo.getDataEmprestimo());
        dto.setDataPagamento(emprestimo.getDataPagamento());
        dto.setValorEmprestimo(emprestimo.getValorEmprestimo());
        dto.setPorcentagem(emprestimo.getPorcentagem());
        dto.setQuitado(emprestimo.getQuitado());
        dto.setBeneficiario(beneficiarioMapper.paraDto(emprestimo.getBeneficiario()));
        dto.setDataCriacao(emprestimo.getDataCriacao());
        dto.setDataAtualizacao(emprestimo.getDataAtualizacao());
        dto.setObservacao(emprestimo.getObservacao());

        return dto;
    }

    private String obterMensagem(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
