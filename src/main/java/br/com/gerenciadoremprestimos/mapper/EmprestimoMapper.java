package br.com.gerenciadoremprestimos.mapper;

import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
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

    public Emprestimo paraEntidade(EmprestimoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Emprestimo emprestimo = new Emprestimo();
        
        return atualizarCampos(emprestimo, dto);
    }

    public Emprestimo paraEntidadeAtualizar(Emprestimo emprestimo, EmprestimoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return atualizarCampos(emprestimo, dto);
    }

    private Emprestimo atualizarCampos(Emprestimo emprestimo, EmprestimoRequestDTO dto){
        try {
            emprestimo.setDataEmprestimo(Utils.convertStringToLocalDateTime(dto.getDataEmprestimo()));
            emprestimo.setDataPagamento(Utils.convertStringToLocalDateTime(dto.getDataPagamento()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("data.invalida"));
        }

        emprestimo.setValorEmprestimo(dto.getValorEmprestimo());
        emprestimo.setPorcentagem(dto.getPorcentagem());
        emprestimo.setQuitado(dto.getQuitado());
        emprestimo.setBeneficiario(emprestimo.getBeneficiario());

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
        dto.setBeneficiario(null);
        dto.setDataCriacao(emprestimo.getDataCriacao());
        dto.setDataAtualizacao(emprestimo.getDataAtualizacao());
        dto.setObservacao(emprestimo.getObservacao());

        return dto;
    }

    private String obterMensagem(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
