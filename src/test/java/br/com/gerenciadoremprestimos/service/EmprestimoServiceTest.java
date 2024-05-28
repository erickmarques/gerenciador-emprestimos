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
        when(emprestimoMapper.paraEntidade(any())).thenReturn(emprestimo);
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.inserir(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(emprestimoRepository, times(1)).save(any());
    }

    @Test
    void inserir_DeveLancarExcecaoQuandoDataInvalida_BadRequest() {
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

    @Test
    void atualizar_DeveAtualizarEmprestimo() {
        String id = "1";
        when(emprestimoRepository.findById(Long.valueOf(id))).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.paraEntidadeAtualizar(any(Emprestimo.class), any(EmprestimoRequestDTO.class))).thenReturn(emprestimo);
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.atualizar(id, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(emprestimoRepository, times(1)).findById(Long.valueOf(id));
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
    void atualizar_DeveLancarExcecaoQuandoErroAoSalvar_InternalServerError() {
        String id = "1";
        String mensagemErro = "Erro ao salvar empréstimo";
        when(emprestimoRepository.findById(Long.valueOf(id))).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.paraEntidadeAtualizar(any(Emprestimo.class), any(EmprestimoRequestDTO.class))).thenReturn(emprestimo);
        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, mensagemErro)).when(emprestimoRepository).save(any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.atualizar(id, requestDTO);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(emprestimoRepository, times(1)).findById(Long.valueOf(id));
        verify(emprestimoRepository, times(1)).save(emprestimo);
    }

    @Test
    void remover_DeveRemoverEmprestimoComSucesso() {
        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_BENEF))).thenReturn(Optional.of(emprestimo));

        emprestimoService.remover(TestUtils.ID_BENEF);

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


}
