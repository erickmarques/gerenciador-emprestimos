package br.com.gerenciadoremprestimos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emprestimo")
@RequiredArgsConstructor
public class EmprestimoController {
    
    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> inserir(@Valid @RequestBody EmprestimoRequestDTO requestDTO) {

        EmprestimoResponseDTO responseDTO = emprestimoService.inserir(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
