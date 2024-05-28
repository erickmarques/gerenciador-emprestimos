package br.com.gerenciadoremprestimos.mapper;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoResponseDTO;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.util.Utils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PagamentoMapper {

   private final MessageSource messageSource;
   private final EmprestimoMapper emprestimoMapper;

    public Pagamento paraEntidade(PagamentoRequestDTO dto, Emprestimo emprestimo) {
        if (dto == null) {
            return null;
        }
        
        Pagamento pagamento = new Pagamento();
        
        return atualizarCampos(pagamento, dto, emprestimo);
    }

    public Pagamento paraEntidadeAtualizar(Pagamento pagamento, PagamentoRequestDTO dto, Emprestimo emprestimo) {
        if (dto == null) {
            return null;
        }

        return atualizarCampos(pagamento, dto, emprestimo);
    }

    private Pagamento atualizarCampos(Pagamento pagamento, PagamentoRequestDTO dto, Emprestimo emprestimo){
        try {
            pagamento.setDataPagamento(Utils.convertStringToLocalDateTime(dto.getDataPagamento()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("data.invalida"));
        }

        pagamento.setValorPago(dto.getValorPago());
        pagamento.setTipoPagamento(convertToEnum(dto.getTipoPagamento()));
        pagamento.setEmprestimo(emprestimo);

        return pagamento;
    }

    private TipoPagamento convertToEnum(String tipoPagamentoStr) {
        try {
            return TipoPagamento.valueOf(tipoPagamentoStr);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("pagamento.tipoPagamento.invalido"));
        }
    }

    public PagamentoResponseDTO paraDto(Pagamento pagamento) {
        if (pagamento == null) {
            return null;
        }
        PagamentoResponseDTO dto = new PagamentoResponseDTO();

        dto.setId(pagamento.getId());
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setValorPago(pagamento.getValorPago());
        dto.setTipoPagamento(pagamento.getTipoPagamento());
        dto.setEmprestimo(emprestimoMapper.paraDto(pagamento.getEmprestimo()));
        dto.setDataCriacao(pagamento.getDataCriacao());
        dto.setDataAtualizacao(pagamento.getDataAtualizacao());
        dto.setObservacao(pagamento.getObservacao());

        return dto;
    }

    private String obterMensagem(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
    
}
