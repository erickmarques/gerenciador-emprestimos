package br.com.gerenciadoremprestimos.service;

import br.com.gerenciadoremprestimos.TestUtils;
import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.mapper.BeneficiarioMapper;
import br.com.gerenciadoremprestimos.mapper.EmprestimoMapper;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @Mock
    private BeneficiarioMapper beneficiarioMapper;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Mock
    private MessageSource messageSource;

    private Beneficiario beneficiario;
    private Emprestimo emprestimo;
    private EmprestimoRequestDTO requestDTO;
    private EmprestimoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        beneficiario    = TestUtils.criarBeneficiario();
        emprestimo      = TestUtils.criarEmprestimo(beneficiario, TestUtils.VALOR1000, TestUtils.PORCENTAGEM30, TestUtils.DATA_EMPRESTIMO1, TestUtils.DATA_EMPRESTIMO1.plusMonths(1L), false);
        requestDTO      = TestUtils.criarEmprestimoRequestDTO(false, beneficiario);
        responseDTO     = TestUtils.criarEmprestimoResponseDTO(1L, false, beneficiarioMapper.paraDto(beneficiario));
    }

    @Test
    void inserir_DeveInserirEmprestimo() {
        when(emprestimoMapper.paraEntidade(any())).thenReturn(emprestimo);
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.inserir(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(emprestimoRepository, times(1)).save(any());
    }

    @Test
    void inserir_DeveLancarExcecaoQuandoDataInvalida() {
        requestDTO.setDataEmprestimo("9999-99-99");

        String mensagemErro = "Data inválida";

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagemErro))
            .when(emprestimoMapper).paraEntidade(any(EmprestimoRequestDTO.class));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.inserir(requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(emprestimoRepository, never()).save(any());
    }
}
