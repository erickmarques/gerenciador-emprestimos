package br.com.gerenciadoremprestimos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.gerenciadoremprestimos.dto.PagamentoRequestDTO;
import br.com.gerenciadoremprestimos.dto.PagamentoResponseDTO;
import br.com.gerenciadoremprestimos.mapper.EmprestimoMapper;
import br.com.gerenciadoremprestimos.mapper.PagamentoMapper;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.model.Pagamento;
import br.com.gerenciadoremprestimos.model.Pagamento.TipoPagamento;
import br.com.gerenciadoremprestimos.repository.PagamentoRepository;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.EmprestimoUtil;
import br.com.gerenciadoremprestimos.utils.PagamentoUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    @Mock
    private EmprestimoService emprestimoService;

    @Mock
    private PagamentoMapper pagamentoMapper;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @Mock
    private MessageSource messageSource;

    private Emprestimo emprestimo;
    private Pagamento pagamento;
    private Beneficiario beneficiario;
    private PagamentoRequestDTO requestDTO;
    private PagamentoResponseDTO responseDTO;
    
    private List<Pagamento> pagamentos;

    @BeforeEach
    void setUp() {
        beneficiario    = BeneficiarioUtil.criarBeneficiarioPadrao();
        emprestimo      = EmprestimoUtil.criarEmprestimo(beneficiario, TestUtils.VALOR1000, EmprestimoUtil.PORCENTAGEM30, EmprestimoUtil.DATA_EMPRESTIMO1, EmprestimoUtil.DATA_EMPRESTIMO1.plusMonths(1L), false);
        pagamento       = PagamentoUtil.criarPagamento(emprestimo, TestUtils.VALOR1000, PagamentoUtil.DATA_PAGAMENTO1, TipoPagamento.TOTAL);
        requestDTO      = PagamentoUtil.criarPagamentoRequestDTO(emprestimo);
        responseDTO     = PagamentoUtil.criarPagamentoResponsetDTO(emprestimoMapper.paraDto(emprestimo));

        pagamentos = List.of(pagamento);
    }

     @Test
    void inserir_DeveInserirEmprestimo() {
        when(emprestimoService.obterEmprestimo(anyString())).thenReturn(emprestimo);
        when(pagamentoMapper.paraEntidade(requestDTO, emprestimo)).thenReturn(pagamento);
        when(pagamentoMapper.paraDto(pagamento)).thenReturn(responseDTO);
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        PagamentoResponseDTO result = pagamentoService.inserir(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void inserir_DeveLancarExcecaoQuandoDataInvalida_BadRequest() {

        when(emprestimoService.obterEmprestimo(anyString())).thenReturn(emprestimo);

        requestDTO.setDataPagamento("9999-99-99");

        String mensagemErro = "Data inválida";

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagemErro))
            .when(pagamentoMapper).paraEntidade(any(PagamentoRequestDTO.class), any(Emprestimo.class));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.inserir(requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveAtualizarPagamento() {
        when(emprestimoService.obterEmprestimo(anyString())).thenReturn(emprestimo);

        when(pagamentoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(pagamento));
        when(pagamentoMapper.paraEntidadeAtualizar(any(Pagamento.class), any(PagamentoRequestDTO.class), any(Emprestimo.class))).thenReturn(pagamento);
        when(pagamentoMapper.paraDto(any())).thenReturn(responseDTO);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);

        PagamentoResponseDTO result = pagamentoService.atualizar(TestUtils.ID_VALIDO, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(pagamentoRepository, times(1)).findById(Long.valueOf(TestUtils.ID_VALIDO));
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void atualizar_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        String id = "";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.atualizar(id, requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(pagamentoRepository, never()).findById(any());
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveLancarExcecaoQuandoEmprestimoNaoEncontrado_NotFound() {
        String mensagemErro = "Pagamento não encontrado";
        when(pagamentoRepository.findById(TestUtils.ID_INEXISTENTE)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("pagamento.naoExiste"), any(Object[].class), any(Locale.class))).thenReturn(mensagemErro);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.atualizar(String.valueOf(TestUtils.ID_INEXISTENTE), requestDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(pagamentoRepository, times(1)).findById(TestUtils.ID_INEXISTENTE);
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void remover_DeveRemoverPagamentoComSucesso() {
        when(pagamentoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(pagamento));

        pagamentoService.remover(TestUtils.ID_VALIDO);

        verify(pagamentoRepository, times(1)).delete(pagamento);
    }

    @Test
    void remover_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        String id = "";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.remover(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(pagamentoRepository, never()).findById(any());
        verify(pagamentoRepository, never()).delete(any());
    }

    @Test
    void remover_DeveLancarExcecaoQuandoPagamentoNaoEncontrado_NotFound() {
        String mensagemErro = "Pagamento não encontrado";

        when(pagamentoRepository.findById(TestUtils.ID_INEXISTENTE)).thenReturn(Optional.empty());
        when(messageSource.getMessage("pagamento.naoExiste", new Object[]{String.valueOf(TestUtils.ID_INEXISTENTE)}, Locale.getDefault())).thenReturn(mensagemErro);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.remover(String.valueOf(TestUtils.ID_INEXISTENTE));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(mensagemErro, exception.getReason());
        verify(pagamentoRepository, never()).delete(any());
    }

    @Test
    public void buscarTodos_ComRepositorioVazio() {
        when(pagamentoRepository.findAll()).thenReturn(Collections.emptyList());

        List<PagamentoResponseDTO> result = pagamentoService.buscarTodos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(pagamentoRepository, times(1)).findAll();
        verify(pagamentoMapper, never()).paraDto(any());
    }

    @Test
    public void buscarTodos_ComVariosPagamentos() {
        when(pagamentoRepository.findAll()).thenReturn(pagamentos);

        List<PagamentoResponseDTO> result = pagamentoService.buscarTodos();

        assertNotNull(result);
        assertEquals(pagamentos.size(), result.size());
        verify(pagamentoRepository, times(1)).findAll();
        verify(emprestimoMapper, times(pagamentos.size())).paraDto(any());
    }

    @Test
    public void buscarTodos_ComRepositorioRetornandoNulo() {
        when(pagamentoRepository.findAll()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            pagamentoService.buscarTodos();
        });

        verify(pagamentoRepository, times(1)).findAll();
        verify(pagamentoMapper, never()).paraDto(any());
    }

    @Test
    void buscarPorId_ComIdValido() {
        when(pagamentoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(pagamento));
        when(pagamentoMapper.paraDto(pagamento)).thenReturn(responseDTO);

        PagamentoResponseDTO result = pagamentoService.buscarPorId(TestUtils.ID_VALIDO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(pagamentoRepository, times(1)).findById(Long.valueOf(TestUtils.ID_VALIDO));
        verify(pagamentoMapper, times(1)).paraDto(pagamento);
    }

    @Test
    void buscarPorId_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.buscarPorId(TestUtils.ID_INVALIDO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(pagamentoRepository, never()).findById(any());
        verify(pagamentoMapper, never()).paraDto(any());
    }

    @Test
    void buscarPorId_ComIdNulo() {
        String id = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.buscarPorId(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(pagamentoRepository, never()).findById(any());
        verify(pagamentoMapper, never()).paraDto(any());
    }
    
}
