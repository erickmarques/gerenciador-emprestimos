package br.com.gerenciadoremprestimos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoResponseDTO;
import br.com.gerenciadoremprestimos.mapper.PagamentoMapper;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final EmprestimoService emprestimoService;
    private final PagamentoMapper pagamentoMapper;
    
   @Transactional
    public PagamentoResponseDTO inserir(PagamentoRequestDTO requestDTO) {

        Emprestimo emprestimo = emprestimoService.obterEmprestimo(String.valueOf(requestDTO.getEmprestimoId()));

        Pagamento pagamento = pagamentoMapper.paraEntidade(requestDTO, emprestimo);

        pagamentoRepository.save(pagamento);
        
        return pagamentoMapper.paraDto(pagamento);
    }
}
