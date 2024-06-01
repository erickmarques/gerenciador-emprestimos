package br.com.gerenciadoremprestimos.service;

import br.com.gerenciadoremprestimos.dto.EmprestimoRequestDTO;
import br.com.gerenciadoremprestimos.dto.EmprestimoResponseDTO;
import br.com.gerenciadoremprestimos.mapper.BeneficiarioMapper;
import br.com.gerenciadoremprestimos.mapper.EmprestimoMapper;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.model.Emprestimo;
import br.com.gerenciadoremprestimos.repository.EmprestimoRepository;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
import br.com.gerenciadoremprestimos.utils.EmprestimoUtil;
import br.com.gerenciadoremprestimos.utils.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
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

/**
 * Classe de testes para EmprestimoService.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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

    /**
     * Configuração inicial para os testes.
     */
    @BeforeEach
    void setUp() {
        beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();
        emprestimo = EmprestimoUtil.criarEmprestimo(beneficiario, TestUtils.VALOR1000, EmprestimoUtil.PORCENTAGEM30, EmprestimoUtil.DATA_EMPRESTIMO1, EmprestimoUtil.DATA_EMPRESTIMO1.plusMonths(1L), false);
        requestDTO = EmprestimoUtil.criarEmprestimoRequestDTO(false, beneficiario);
        responseDTO = EmprestimoUtil.criarEmprestimoResponseDTO(1L, false, beneficiarioMapper.paraDto(beneficiario));

        emprestimos = List.of(emprestimo);
    }

    /**
     * Testa a inserção de um empréstimo.
     */
    @Test
    @DisplayName("Deve inserir um empréstimo com sucesso")
    void inserir_DeveInserirEmprestimo() {
        when(beneficiarioService.obterBeneficiario(anyString())).thenReturn(beneficiario);
        when(emprestimoMapper.paraEntidade(requestDTO, beneficiario)).thenReturn(emprestimo); 
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.inserir(requestDTO);

        assertNotNull(result, "O resultado não deve ser nulo");
        assertEquals(responseDTO, result, "O resultado deve ser igual ao esperado");

        verify(emprestimoRepository, times(1)).save(emprestimo);
    }

    /**
     * Testa a inserção de um empréstimo com data inválida.
     */
    @Test
    @DisplayName("Deve lançar exceção quando a data do empréstimo for inválida")
    void inserir_DeveLancarExcecaoQuandoDataInvalida_BadRequest() {
        when(beneficiarioService.obterBeneficiario(anyString())).thenReturn(beneficiario);

        requestDTO.setDataEmprestimo("9999-99-99");

        String mensagemErro = "Data inválida";

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagemErro))
            .when(emprestimoMapper).paraEntidade(any(EmprestimoRequestDTO.class), any(Beneficiario.class));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.inserir(requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "A exceção deve ter o status BAD_REQUEST");
        assertEquals(mensagemErro, exception.getReason(), "A razão da exceção deve ser a mensagem de erro");
        verify(emprestimoRepository, never()).save(any());
    }

    /**
     * Testa a atualização de um empréstimo.
     */
    @Test
    @DisplayName("Deve atualizar um empréstimo com sucesso")
    void atualizar_DeveAtualizarEmprestimo() {
        when(beneficiarioService.obterBeneficiario(anyString())).thenReturn(beneficiario);
        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.paraEntidadeAtualizar(any(Emprestimo.class), any(EmprestimoRequestDTO.class), any(Beneficiario.class))).thenReturn(emprestimo);
        when(emprestimoMapper.paraDto(any())).thenReturn(responseDTO);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);

        EmprestimoResponseDTO result = emprestimoService.atualizar(TestUtils.ID_VALIDO, requestDTO);

        assertNotNull(result, "O resultado não deve ser nulo");
        assertEquals(responseDTO, result, "O resultado deve ser igual ao esperado");

        verify(emprestimoRepository, times(1)).findById(Long.valueOf(TestUtils.ID_VALIDO));
        verify(emprestimoRepository, times(1)).save(emprestimo);
    }

    /**
     * Testa a atualização de um empréstimo com ID inválido.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o ID do empréstimo for inválido")
    void atualizar_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        String id = "";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.atualizar(id, requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "A exceção deve ter o status BAD_REQUEST");
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoRepository, never()).save(any());
    }

    /**
     * Testa a atualização de um empréstimo quando o empréstimo não for encontrado.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o empréstimo não for encontrado")
    void atualizar_DeveLancarExcecaoQuandoEmprestimoNaoEncontrado_NotFound() {
        String mensagemErro = "Empréstimo não encontrado";
        when(emprestimoRepository.findById(TestUtils.ID_INEXISTENTE)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("emprestimo.naoExiste"), any(Object[].class), any(Locale.class))).thenReturn(mensagemErro);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.atualizar(String.valueOf(TestUtils.ID_INEXISTENTE), requestDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "A exceção deve ter o status NOT_FOUND");
        assertEquals(mensagemErro, exception.getReason(), "A razão da exceção deve ser a mensagem de erro");
        verify(emprestimoRepository, times(1)).findById(TestUtils.ID_INEXISTENTE);
        verify(emprestimoRepository, never()).save(any());
    }

    /**
     * Testa a remoção de um empréstimo com sucesso.
     */
    @Test
    @DisplayName("Deve remover um empréstimo com sucesso")
    void remover_DeveRemoverEmprestimoComSucesso() {
        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(emprestimo));

        emprestimoService.remover(TestUtils.ID_VALIDO);

        verify(emprestimoRepository, times(1)).delete(emprestimo);
    }

    /**
     * Testa a remoção de um empréstimo com ID inválido.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o ID do empréstimo for inválido")
    void remover_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        String id = "";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.remover(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "A exceção deve ter o status BAD_REQUEST");
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoRepository, never()).delete(any());
    }

    /**
     * Testa a remoção de um empréstimo quando o empréstimo não for encontrado.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o empréstimo não for encontrado")
    void remover_DeveLancarExcecaoQuandoEmprestimoNaoEncontrado_NotFound() {
        String mensagemErro = "Empréstimo não encontrado";

        when(emprestimoRepository.findById(TestUtils.ID_INEXISTENTE)).thenReturn(Optional.empty());
        when(messageSource.getMessage("emprestimo.naoExiste", new Object[]{String.valueOf(TestUtils.ID_INEXISTENTE)}, Locale.getDefault())).thenReturn(mensagemErro);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.remover(String.valueOf(TestUtils.ID_INEXISTENTE));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "A exceção deve ter o status NOT_FOUND");
        assertEquals(mensagemErro, exception.getReason(), "A razão da exceção deve ser a mensagem de erro");
        verify(emprestimoRepository, never()).delete(any());
    }

    /**
     * Testa a busca de todos os empréstimos quando o repositório estiver vazio.
     */
    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver empréstimos")
    void buscarTodos_ComRepositorioVazio() {
        when(emprestimoRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmprestimoResponseDTO> result = emprestimoService.buscarTodos();

        assertNotNull(result, "O resultado não deve ser nulo");
        assertTrue(result.isEmpty(), "A lista de resultados deve estar vazia");
        verify(emprestimoRepository, times(1)).findAll();
        verify(emprestimoMapper, never()).paraDto(any());
    }

    /**
     * Testa a busca de todos os empréstimos quando houver vários empréstimos.
     */
    @Test
    @DisplayName("Deve retornar uma lista com todos os empréstimos")
    void buscarTodos_ComVariosEmprestimos() {
        when(emprestimoRepository.findAll()).thenReturn(emprestimos);

        List<EmprestimoResponseDTO> result = emprestimoService.buscarTodos();

        assertNotNull(result, "O resultado não deve ser nulo");
        assertEquals(emprestimos.size(), result.size(), "O tamanho da lista de resultados deve ser igual ao número de empréstimos");
        verify(emprestimoRepository, times(1)).findAll();
        verify(emprestimoMapper, times(emprestimos.size())).paraDto(any());
    }

    /**
     * Testa a busca de todos os empréstimos quando o repositório retornar nulo.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o repositório retornar nulo")
    void buscarTodos_ComRepositorioRetornandoNulo() {
        when(emprestimoRepository.findAll()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            emprestimoService.buscarTodos();
        });

        verify(emprestimoRepository, times(1)).findAll();
        verify(emprestimoMapper, never()).paraDto(any());
    }

    /**
     * Testa a busca de um empréstimo por ID válido.
     */
    @Test
    @DisplayName("Deve buscar um empréstimo por ID com sucesso")
    void buscarPorId_ComIdValido() {
        when(emprestimoRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.paraDto(emprestimo)).thenReturn(responseDTO);

        EmprestimoResponseDTO result = emprestimoService.buscarPorId(TestUtils.ID_VALIDO);

        assertNotNull(result, "O resultado não deve ser nulo");
        assertEquals(responseDTO, result, "O resultado deve ser igual ao esperado");
        verify(emprestimoRepository, times(1)).findById(Long.valueOf(TestUtils.ID_VALIDO));
        verify(emprestimoMapper, times(1)).paraDto(emprestimo);
    }

    /**
     * Testa a busca de um empréstimo por ID inválido.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o ID do empréstimo for inválido")
    void buscarPorId_DeveLancarExcecaoQuandoIdInvalido_BadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.buscarPorId(TestUtils.ID_INVALIDO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "A exceção deve ter o status BAD_REQUEST");
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoMapper, never()).paraDto(any());
    }

    /**
     * Testa a busca de um empréstimo por ID nulo.
     */
    @Test
    @DisplayName("Deve lançar exceção quando o ID do empréstimo for nulo")
    void buscarPorId_ComIdNulo() {
        String id = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emprestimoService.buscarPorId(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "A exceção deve ter o status BAD_REQUEST");
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoMapper, never()).paraDto(any());
    }
}
