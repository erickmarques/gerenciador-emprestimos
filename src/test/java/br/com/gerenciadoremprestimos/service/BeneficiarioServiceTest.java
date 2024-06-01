package br.com.gerenciadoremprestimos.service;

import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.mapper.BeneficiarioMapper;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import br.com.gerenciadoremprestimos.utils.BeneficiarioUtil;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para o BeneficiarioService.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @Mock
    private BeneficiarioMapper beneficiarioMapper;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    @Mock
    private MessageSource messageSource;

    private Beneficiario beneficiario;
    private Beneficiario beneficiario2;
    private Beneficiario beneficiario3;
    private Beneficiario beneficiario4;
    private BeneficiarioRequestDTO requestDTO;
    private BeneficiarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        beneficiario = BeneficiarioUtil.criarBeneficiarioPadrao();        
        beneficiario2 = BeneficiarioUtil.criarBeneficiario("EDSON MARQUES", "081955554222", "OBS TESTE");
        beneficiario3 = BeneficiarioUtil.criarBeneficiario("CAUA MARQUES", "081955554333", "OBS TESTE");
        beneficiario4 = BeneficiarioUtil.criarBeneficiario("LAURA ANDRADE", "081955554444", "OBS TESTE");

        requestDTO = BeneficiarioUtil.criaBeneficiarioRequestDTO();
        responseDTO = BeneficiarioUtil.criaBeneficiarioResponseDTO();
    }

    /**
     * Teste para verificar a inserção de um beneficiário.
     */
    @Test
    @DisplayName("Deve inserir um novo beneficiário")
    void inserir_DeveInserirBeneficiario() {
        when(beneficiarioMapper.paraEntidade(any())).thenReturn(beneficiario);
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);
        when(beneficiarioRepository.save(any())).thenReturn(beneficiario);

        BeneficiarioResponseDTO result = beneficiarioService.inserir(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);

        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    /**
     * Teste para verificar a atualização de um beneficiário.
     */
    @Test
    @DisplayName("Deve atualizar um beneficiário existente")
    void atualizar_DeveAtualizarBeneficiario() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.of(beneficiario));
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);

        BeneficiarioResponseDTO result = beneficiarioService.atualizar(TestUtils.ID_VALIDO, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);

        verify(beneficiarioRepository, times(1)).save(any());
    }

    /**
     * Teste para verificar a exceção ao atualizar um beneficiário com ID inválido.
     */
    @Test
    @DisplayName("Deve lançar exceção BadRequest ao atualizar um beneficiário com ID inválido")
    void atualizar_DeveLancarExcecao_BadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> beneficiarioService.atualizar(TestUtils.ID_INVALIDO, new BeneficiarioRequestDTO()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Teste para verificar a exceção ao atualizar um beneficiário que não existe.
     */
    @Test
    @DisplayName("Deve lançar exceção NotFound ao atualizar um beneficiário inexistente")
    void atualizar_DeveLancarExcecao_NotFound() {
        when(beneficiarioRepository.findById(Long.valueOf(TestUtils.ID_VALIDO))).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mensagem de erro");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> beneficiarioService.atualizar(TestUtils.ID_VALIDO, new BeneficiarioRequestDTO()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Teste para verificar a remoção de um beneficiário.
     */
    @Test
    @DisplayName("Deve remover um beneficiário existente")
    void remover_DeveRemoverBeneficiario() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.of(beneficiario));

        assertDoesNotThrow(() -> beneficiarioService.remover(TestUtils.ID_VALIDO));

        verify(beneficiarioRepository, times(1)).delete(any());
    }

    /**
     * Teste para verificar a exceção ao remover um beneficiário não encontrado.
     */
    @Test
    @DisplayName("Deve lançar exceção NotFound ao remover um beneficiário não encontrado")
    void remover_BeneficiarioNaoEncontrado_DeveLancarExcecao_NotFound() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mensagem de erro");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> beneficiarioService.remover(TestUtils.ID_VALIDO));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(beneficiarioRepository, never()).delete(any());
    }

    /**
     * Teste para verificar a exceção ao remover um beneficiário com ID inválido.
     */
    @Test
    @DisplayName("Deve lançar exceção BadRequest ao remover um beneficiário com ID inválido")
    void remover_BeneficiarioNaoEncontrado_DeveLancarExcecao_BadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> beneficiarioService.remover(TestUtils.ID_INVALIDO));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(beneficiarioRepository, never()).delete(any());
    }

    /**
     * Teste para verificar a busca de todos os beneficiários.
     */
    @Test
    @DisplayName("Deve retornar uma lista de beneficiários")
    void buscarTodos_DeveRetornarListaDeBeneficiarios() {
        when(beneficiarioRepository.findAll()).thenReturn(Arrays.asList(beneficiario));
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);

        List<BeneficiarioResponseDTO> result = beneficiarioService.buscarTodos();

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(responseDTO);

        verify(beneficiarioRepository, times(1)).findAll();
    }

    /**
     * Teste para verificar a busca de um beneficiário por ID.
     */
    @Test
    @DisplayName("Deve retornar um beneficiário pelo ID")
    void buscarPorId_DeveRetornarBeneficiarioPeloId() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.of(beneficiario));
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);

        BeneficiarioResponseDTO result = beneficiarioService.buscarPorId(TestUtils.ID_VALIDO);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);

        verify(beneficiarioRepository, times(1)).findById(anyLong());
    }

    /**
     * Teste para verificar a exceção ao buscar um beneficiário que não existe.
     */
    @Test
    @DisplayName("Deve lançar exceção ao buscar um beneficiário inexistente")
    void buscarPorId_BeneficiarioNaoEncontrado_DeveLancarExcecao() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mensagem de erro");

        assertThrows(ResponseStatusException.class, () -> beneficiarioService.buscarPorId(TestUtils.ID_VALIDO));

        verify(beneficiarioRepository, times(1)).findById(anyLong());
    }

    /**
     * Teste para verificar a busca de beneficiários por nome.
     */
    @Test
    @DisplayName("Deve retornar uma lista de beneficiários pelo nome")
    void buscarPorNome_DeveRetornarListaDeBeneficiarios() {
        when(beneficiarioRepository.findByNomeContainingIgnoreCase(anyString()))
                .thenReturn(Arrays.asList(beneficiario, beneficiario2, beneficiario3, beneficiario4));
        when(beneficiarioMapper.paraDto(any(Beneficiario.class))).thenReturn(responseDTO);

        List<BeneficiarioResponseDTO> result = beneficiarioService.buscarPorNome(anyString());

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(4);

        for (BeneficiarioResponseDTO dto : result) {
            assertThat(dto).isEqualTo(responseDTO);
        }

        verify(beneficiarioRepository, times(1)).findByNomeContainingIgnoreCase(any());
        verify(beneficiarioMapper, times(4)).paraDto(any(Beneficiario.class));
    }
}
