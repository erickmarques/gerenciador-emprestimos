package br.com.gerenciadoremprestimos.service;

import br.com.gerenciadoremprestimos.TestUtils;
import br.com.gerenciadoremprestimos.dto.BeneficiarioRequestDTO;
import br.com.gerenciadoremprestimos.dto.BeneficiarioResponseDTO;
import br.com.gerenciadoremprestimos.mapper.BeneficiarioMapper;
import br.com.gerenciadoremprestimos.model.Beneficiario;
import br.com.gerenciadoremprestimos.repository.BeneficiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        beneficiario  = TestUtils.criarBeneficiario();        
        beneficiario2 = criarBeneficiario("EDSON MARQUES", "081955554222", "OBS TESTE");
        beneficiario3 = criarBeneficiario("CAUA MARQUES", "081955554333", "OBS TESTE");
        beneficiario4 = criarBeneficiario("LAURA ANDRADE", "081955554444", "OBS TESTE");

        requestDTO    = TestUtils.criaBeneficiarioRequestDTO();
        responseDTO   = TestUtils.criaBeneficiarioResponseDTO();
    }

    @Test
    void inserir_DeveInserirBeneficiario() {
        when(beneficiarioMapper.paraEntidade(any())).thenReturn(beneficiario);
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);
        when(beneficiarioRepository.save(any())).thenReturn(beneficiario);

        BeneficiarioResponseDTO result = beneficiarioService.inserir(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    @Test
    void atualizar_DeveAtualizarBeneficiario() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.of(beneficiario));
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);

        BeneficiarioResponseDTO result = beneficiarioService.atualizar(TestUtils.ID_VALIDO, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(beneficiarioRepository, times(1)).save(any());
    }

    @Test
    void atualizar_DeveLancarExcecao_BadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> beneficiarioService.atualizar(TestUtils.ID_INVALIDO, new BeneficiarioRequestDTO()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode()); 
    }

    @Test
    void atualizar_DeveLancarExcecao_NotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> beneficiarioService.atualizar(TestUtils.ID_VALIDO, new BeneficiarioRequestDTO()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()); 
    }

    @Test
    void remover_DeveRemoverBeneficiario() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.of(beneficiario));

        assertDoesNotThrow(() -> beneficiarioService.remover(TestUtils.ID_VALIDO));

        verify(beneficiarioRepository, times(1)).delete(any());
    }

    @Test
    void remover_BeneficiarioNaoEncontrado_DeveLancarExcecao_NotFound() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mensagem de erro");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> beneficiarioService.remover(TestUtils.ID_VALIDO));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()); 

        verify(beneficiarioRepository, never()).delete(any());
    }

    @Test
    void remover_BeneficiarioNaoEncontrado_DeveLancarExcecao_BadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> beneficiarioService.remover(TestUtils.ID_INVALIDO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode()); 

        verify(beneficiarioRepository, never()).delete(any());
    }

    @Test
    void buscarTodos_DeveRetornarListaDeBeneficiarios() {
        when(beneficiarioRepository.findAll()).thenReturn(Arrays.asList(beneficiario));
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);

        List<BeneficiarioResponseDTO> result = beneficiarioService.buscarTodos();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));

        verify(beneficiarioRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_DeveRetornarBeneficiarioPeloId() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.of(beneficiario));
        when(beneficiarioMapper.paraDto(any())).thenReturn(responseDTO);

        BeneficiarioResponseDTO result = beneficiarioService.buscarPorId(TestUtils.ID_VALIDO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(beneficiarioRepository, times(1)).findById(anyLong());
    }

    @Test
    void buscarPorId_BeneficiarioNaoEncontrado_DeveLancarExcecao() {
        when(beneficiarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mensagem de erro");

        assertThrows(ResponseStatusException.class, () -> beneficiarioService.buscarPorId(TestUtils.ID_VALIDO));

        verify(beneficiarioRepository, times(1)).findById(anyLong());
    }

    @Test
    void buscarPorNome_DeveRetornarListaDeBeneficiarios() {

        when(beneficiarioRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(Arrays.asList(beneficiario, beneficiario2, beneficiario3, beneficiario4));
        when(beneficiarioMapper.paraDto(any(Beneficiario.class))).thenReturn(responseDTO);

        List<BeneficiarioResponseDTO> result = beneficiarioService.buscarPorNome(anyString());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(4, result.size());

        // Verify that all returned DTOs are correct
        for (BeneficiarioResponseDTO dto : result) {
            assertEquals(responseDTO, dto);
        }

        verify(beneficiarioRepository, times(1)).findByNomeContainingIgnoreCase(any());
        verify(beneficiarioMapper, times(4)).paraDto(any(Beneficiario.class));
    }

    private Beneficiario criarBeneficiario(String nome, String telefone, String observacao){
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setNome(nome);
        beneficiario.setNumeroTelefone(telefone);
        beneficiario.setObservacao(observacao);
        return beneficiario;
    }
}
