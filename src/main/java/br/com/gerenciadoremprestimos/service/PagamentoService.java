package br.com.gerenciadoremprestimos.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoResponseDTO;
import br.com.gerenciadoremprestimos.mapper.PagamentoMapper;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.repository.PagamentoRepository;
import br.com.gerenciadoremprestimos.util.Utils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final EmprestimoService emprestimoService;
    private final PagamentoMapper pagamentoMapper;
    private final MessageSource messageSource;
    
   @Transactional
    public PagamentoResponseDTO inserir(PagamentoRequestDTO requestDTO) {

        Emprestimo emprestimo = emprestimoService.obterEmprestimo(String.valueOf(requestDTO.getEmprestimoId()));

        Pagamento pagamento = pagamentoMapper.paraEntidade(requestDTO, emprestimo);

        pagamentoRepository.save(pagamento);
        
        return pagamentoMapper.paraDto(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO atualizar(String id, PagamentoRequestDTO requestDTO) {

        validarId(id);

        Emprestimo emprestimo = emprestimoService.obterEmprestimo(String.valueOf(requestDTO.getEmprestimoId()));
        Pagamento pagamento   = obterPagamento(id);
        pagamento             = pagamentoMapper.paraEntidadeAtualizar(pagamento, requestDTO, emprestimo);

        pagamentoRepository.save(pagamento);
        
        return pagamentoMapper.paraDto(pagamento);
    }

    @Transactional
    public void remover(String id) {
        validarId(id);
        Pagamento pagamento = obterPagamento(id);
        pagamentoRepository.delete(pagamento);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarTodos() {
        return pagamentoRepository.findAll()
                .stream()
                .map(pagamentoMapper::paraDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorId(String id) {
        validarId(id);
        Pagamento pagamento = obterPagamento(id);
        return pagamentoMapper.paraDto(pagamento);
    }

    public void validarId(String id){
        if (!Utils.contemApenasNumeros(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("pagamento.idInvalido", id));
        }
    }

    public Pagamento obterPagamento(String id){
        return pagamentoRepository.findById(Long.valueOf(id))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, obterMensagem("pagamento.naoExiste", id)));
    }

    private String obterMensagem(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
