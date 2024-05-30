package br.com.gerenciadoremprestimos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.service.BeneficiarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/beneficiario")
@RequiredArgsConstructor
public class BeneficiarioController {
    
    private final BeneficiarioService beneficiarioService;

    @PostMapping
    public ResponseEntity<BeneficiarioResponseDTO> inserir(@Valid @RequestBody BeneficiarioRequestDTO requestDTO) {

        BeneficiarioResponseDTO responseDTO = beneficiarioService.inserir(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(@PathVariable String id, @Valid @RequestBody BeneficiarioRequestDTO requestDTO) {
        BeneficiarioResponseDTO responseDTO = beneficiarioService.atualizar(id, requestDTO);
        if (responseDTO != null) {
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        beneficiarioService.remover(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(@PathVariable String id) {
        BeneficiarioResponseDTO beneficiario = beneficiarioService.buscarPorId(id);
        if (beneficiario != null) {
            return new ResponseEntity<>(beneficiario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioResponseDTO>> buscarTodos() {
        List<BeneficiarioResponseDTO> beneficiarios = beneficiarioService.buscarTodos();
        return new ResponseEntity<>(beneficiarios, HttpStatus.OK);
    }

    @GetMapping("/buscarPorNome/{nome}")
    public ResponseEntity<List<BeneficiarioResponseDTO>> buscarPorNome(@PathVariable String nome) {
        List<BeneficiarioResponseDTO> beneficiarios = beneficiarioService.buscarPorNome(nome);
        return new ResponseEntity<>(beneficiarios, HttpStatus.OK);
    }

    @PostMapping("/{id}/imagem")
    public ResponseEntity<Void> salvarImagem(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) {
        beneficiarioService.salvarImagem(id, file);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
