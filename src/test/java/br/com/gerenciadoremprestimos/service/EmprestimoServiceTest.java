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

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    private BeneficiarioService beneficiarioService;

    @Mock
    private MessageSource messageSource;

    private Beneficiario beneficiario;
    private Emprestimo emprestimo;
    private EmprestimoRequestDTO requestDTO;
    private EmprestimoResponseDTO responseDTO;
    
    private List<Emprestimo> emprestimos;

    @BeforeEach
    void setUp() {
        beneficiario    = TestUtils.criarBeneficiario();
        emprestimo      = TestUtils.criarEmprestimo(beneficiario, TestUtils.VALOR1000, TestUtils.PORCENTAGEM30, TestUtils.DATA_EMPRESTIMO1, TestUtils.DATA_EMPRESTIMO1.plusMonths(1L), false);
        requestDTO      = TestUtils.criarEmprestimoRequestDTO(false, beneficiario);
        responseDTO     = TestUtils.criarEmprestimoResponseDTO(1L, false, beneficiarioMapper.paraDto(beneficiario));

        emprestimos = List.of(emprestimo);
    }

    @Test
    void inserir_DeveInserirEmprestimo() {
        when(beneficiarioService.obterBeneficiario(anyString())).thenReturn(beneficiario);
        when(emprestimoMapper.paraEntidade(requestDTO, beneficiario)).thenReturn(emprestimo); 
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.inserir(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(emprestimoRepository, times(1)).save(emprestimo);
    }

    @Test
    void inserir_DeveLancarExcecaoQuandoDataInvalida_BadRequest() {

        when(beneficiarioService.obterBeneficiario(anyString())).thenReturn(beneficiario);

        requestDTO.setDataEmprestimo("9999-99-99");

        String mensagemErro = "Data inválida";

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagemErro))
            .when(emprestimoMapper).paraEntidade(any(EmprestimoRequestDTO.class), any(Beneficiario.class));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.inserir(requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveAtualizarEmprestimo() {
        when(beneficiarioService.obterBeneficiario(anyString())).thenReturn(beneficiario);

        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.paraEntidadeAtualizar(any(Emprestimo.class), any(EmprestimoRequestDTO.class), any(Beneficiario.class))).thenReturn(emprestimo);
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.atualizar(TestUtils.ID_VALIDO, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(emprestimoRepository, times(1)).findById(Long.valueOf(TestUtils.ID_VALIDO));
        verify(emprestimoRepository, times(1)).save(emprestimo);
    }

    @Test
    void atualizar_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        String id = "";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.atualizar(id, requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveLancarExcecaoQuandoEmprestimoNaoEncontrado_NotFound() {
        String mensagemErro = "Empréstimo não encontrado";
        when(emprestimoRepository.findById(TestUtils.ID_INEXISTENTE)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("emprestimo.naoExiste"), any(Object[].class), any(Locale.class))).thenReturn(mensagemErro);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.atualizar(String.valueOf(TestUtils.ID_INEXISTENTE), requestDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(emprestimoRepository, times(1)).findById(TestUtils.ID_INEXISTENTE);
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    void remover_DeveRemoverEmprestimoComSucesso() {
        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(emprestimo));

        emprestimoService.remover(TestUtils.ID_VALIDO);

        verify(emprestimoRepository, times(1)).delete(emprestimo);
    }

    @Test
    void remover_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        String id = "";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.remover(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoRepository, never()).delete(any());
    }

    @Test
    void remover_DeveLancarExcecaoQuandoEmprestimoNaoEncontrado_NotFound() {
        String mensagemErro = "Empréstimo não encontrado";

        when(emprestimoRepository.findById(TestUtils.ID_INEXISTENTE)).thenReturn(Optional.empty());
        when(messageSource.getMessage("emprestimo.naoExiste", new Object[]{String.valueOf(TestUtils.ID_INEXISTENTE)}, Locale.getDefault())).thenReturn(mensagemErro);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.remover(String.valueOf(TestUtils.ID_INEXISTENTE));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(emprestimoRepository, never()).delete(any());
    }

    @Test
    public void buscarTodos_ComRepositorioVazio() {
        when(emprestimoRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmprestimoResponseDTO> result = emprestimoService.buscarTodos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(emprestimoRepository, times(1)).findAll();
        verify(emprestimoMapper, never()).paraDto(any());
    }

    @Test
    public void buscarTodos_ComVariosEmprestimos() {
        when(emprestimoRepository.findAll()).thenReturn(emprestimos);

        List<EmprestimoResponseDTO> result = emprestimoService.buscarTodos();

        assertNotNull(result);
        assertEquals(emprestimos.size(), result.size());
        verify(emprestimoRepository, times(1)).findAll();
        verify(emprestimoMapper, times(emprestimos.size())).paraDto(any());
    }

    @Test
    public void buscarTodos_ComRepositorioRetornandoNulo() {
        when(emprestimoRepository.findAll()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            emprestimoService.buscarTodos();
        });

        verify(emprestimoRepository, times(1)).findAll();
        verify(emprestimoMapper, never()).paraDto(any());
    }

    @Test
    void buscarPorId_ComIdValido() {
        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.paraDto(emprestimo)).thenReturn(responseDTO);

        EmprestimoResponseDTO result = emprestimoService.buscarPorId(TestUtils.ID_VALIDO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(emprestimoRepository, times(1)).findById(Long.valueOf(TestUtils.ID_VALIDO));
        verify(emprestimoMapper, times(1)).paraDto(emprestimo);
    }

    @Test
    void buscarPorId_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.buscarPorId(TestUtils.ID_INVALIDO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoMapper, never()).paraDto(any());
    }

    @Test
    void buscarPorId_ComIdNulo() {
        String id = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.buscarPorId(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoMapper, never()).paraDto(any());
    }

}
