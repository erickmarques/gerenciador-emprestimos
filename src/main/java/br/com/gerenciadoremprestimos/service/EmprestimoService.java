package br.com.gerenciadoremprestimos.service;

import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.mapper.EmprestimoMapper;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import br.com.gerenciadoremprestimos.util.Utils;
import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final MessageSource messageSource;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional
    public EmprestimoResponseDTO inserir(EmprestimoRequestDTO requestDTO) {
        Emprestimo emprestimo = emprestimoMapper.paraEntidade(requestDTO);

        emprestimoRepository.save(emprestimo);
        
        return emprestimoMapper.paraDto(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO atualizar(String id, EmprestimoRequestDTO requestDTO) {

        validarId(id);
        
        Emprestimo emprestimo = obterEmprestimo(id);
        emprestimo = emprestimoMapper.paraEntidadeAtualizar(emprestimo, requestDTO);

        emprestimoRepository.save(emprestimo);
        
        return emprestimoMapper.paraDto(emprestimo);
    }

    @Transactional
    public void remover(String id) {
        validarId(id);
        Emprestimo emprestimo = obterEmprestimo(id);
        emprestimoRepository.delete(emprestimo);
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> buscarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(emprestimoMapper::paraDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO buscarPorId(String id) {
        validarId(id);
        Emprestimo emprestimo = obterEmprestimo(id);
        return emprestimoMapper.paraDto(emprestimo);
    }

    public void validarId(String id){
        if (!Utils.contemApenasNumeros(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("emprestimo.idInvalido", id));
        }
    }

    private Emprestimo obterEmprestimo(String id){
        return emprestimoRepository.findById(Long.valueOf(id))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, obterMensagem("emprestimo.naoExiste", id)));
    }

    private String obterMensagem(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

}
