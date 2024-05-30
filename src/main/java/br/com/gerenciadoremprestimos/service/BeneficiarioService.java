package br.com.gerenciadoremprestimos.service;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.mapper.BeneficiarioMapper;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.util.Utils;
import lombok.RequiredArgsConstructor;

import org.apache.tika.Tika;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final MessageSource messageSource;
    private final BeneficiarioMapper beneficiarioMapper;
    
    @Transactional
    public BeneficiarioResponseDTO inserir(BeneficiarioRequestDTO requestDTO) {
        Beneficiario beneficiario = beneficiarioMapper.paraEntidade(requestDTO);

        beneficiarioRepository.save(beneficiario);
        
        return beneficiarioMapper.paraDto(beneficiario);
    }

    @Transactional
    public BeneficiarioResponseDTO atualizar(String id, BeneficiarioRequestDTO requestDTO) {

        validarId(id);
        
        Beneficiario beneficiario = obterBeneficiario(id);
        beneficiario = beneficiarioMapper.paraEntidadeAtualizar(beneficiario, requestDTO);

        beneficiarioRepository.save(beneficiario);
        
        return beneficiarioMapper.paraDto(beneficiario);
    }

    @Transactional
    public void remover(String id) {
        validarId(id);
        Beneficiario beneficiario = obterBeneficiario(id);
        beneficiarioRepository.delete(beneficiario);
    }

    @Transactional(readOnly = true)
    public List<BeneficiarioResponseDTO> buscarTodos() {
        return beneficiarioRepository.findAll()
                .stream()
                .map(beneficiarioMapper::paraDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BeneficiarioResponseDTO buscarPorId(String id) {
        validarId(id);
        Beneficiario beneficiario = obterBeneficiario(id);
        return beneficiarioMapper.paraDto(beneficiario);
    }

    
    @Transactional(readOnly = true)
    public List<BeneficiarioResponseDTO> buscarPorNome(String nome) {
        return beneficiarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(beneficiarioMapper::paraDto)
                .collect(Collectors.toList());
    }

    public void salvarImagem(String id, MultipartFile file)   {
        validarId(id);

        try {
            Beneficiario beneficiario = obterBeneficiario(id);

            if (!isImage(file)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("beneficiario.arquivoDeveSerImagem"));
            }

            beneficiario.setImagem(file.getBytes());

            beneficiarioRepository.save(beneficiario);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("beneficiario.falhaAoSalvarImagem"));
        }

    }
    
    private boolean isImage(MultipartFile file) throws IOException {
        Tika tika = new Tika();

        String mimeType = tika.detect(file.getBytes());
        return mimeType.startsWith("image/");
    }

    private void validarId(String id){
        if (!Utils.contemApenasNumeros(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, obterMensagem("beneficiario.idInvalido", id));
        }
    }

    public Beneficiario obterBeneficiario(String id){
        return beneficiarioRepository.findById(Long.valueOf(id))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, obterMensagem("beneficiario.naoExiste", id)));
    }

    private String obterMensagem(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

}
