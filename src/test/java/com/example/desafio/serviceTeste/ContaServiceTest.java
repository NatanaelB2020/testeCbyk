package com.example.desafio.serviceTeste;

import com.example.desafio.entidade.Conta;
import com.example.desafio.entidade.SituacaoConta;
import com.example.desafio.repository.ContaRepository;
import com.example.desafio.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateConta() {
        Conta conta = new Conta(LocalDate.now(), null, BigDecimal.valueOf(100), "Teste", SituacaoConta.PENDENTE);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta createdConta = contaService.createConta(conta);

        assertNotNull(createdConta);
        assertEquals(conta.getDescricao(), createdConta.getDescricao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    void testAtualizarConta() {
        Long contaId = 1L;
        Conta contaExistente = new Conta(LocalDate.now(), null, BigDecimal.valueOf(100), "Teste", SituacaoConta.PENDENTE);
        Conta contaAtualizada = new Conta(null, LocalDate.now(), BigDecimal.valueOf(150), "Atualizada", SituacaoConta.PAGA);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(contaExistente));
        when(contaRepository.save(any(Conta.class))).thenReturn(contaExistente);

        Conta updatedConta = contaService.atualizarConta(contaId, contaAtualizada);

        assertNotNull(updatedConta);
        assertEquals(LocalDate.now(), updatedConta.getDataPagamento());
        assertEquals(BigDecimal.valueOf(150), updatedConta.getValor());
        verify(contaRepository, times(1)).save(contaExistente);
    }

    @Test
    void testAlterarSituacao() {
        Long contaId = 1L;
        Conta conta = new Conta(LocalDate.now(), null, BigDecimal.valueOf(100), "Teste", SituacaoConta.PENDENTE);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.alterarSituacao(contaId, SituacaoConta.PAGA);

        assertEquals(SituacaoConta.PAGA, conta.getSituacao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    void testListarContas() {
        // Teste para listar contas - você pode implementar esse teste com uma mock Page
    }

    @Test
    void testBuscarPorId() {
        Long contaId = 1L;
        Conta conta = new Conta(LocalDate.now(), null, BigDecimal.valueOf(100), "Teste", SituacaoConta.PENDENTE);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        Optional<Conta> foundConta = contaService.buscarPorId(contaId);

        assertTrue(foundConta.isPresent());
        assertEquals("Teste", foundConta.get().getDescricao());
    }

    @Test
    void testObterValorTotalPago() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        BigDecimal expectedTotal = BigDecimal.valueOf(300);

        when(contaRepository.calcularTotalPago(SituacaoConta.PAGA, startDate, endDate)).thenReturn(expectedTotal);

        BigDecimal totalPago = contaService.obterValorTotalPago(startDate, endDate);

        assertEquals(expectedTotal, totalPago);
    }

    @Test
    void testValidarConta_ThrowsException_ValorInvalido() {
        Conta contaInvalida = new Conta(LocalDate.now(), null, BigDecimal.valueOf(-100), "Teste", SituacaoConta.PENDENTE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            contaService.validarConta(contaInvalida);
        });

        assertEquals("O valor da conta deve ser positivo.", exception.getMessage());
    }

    @Test
    void testValidarConta_ThrowsException_DataPagamentoAnterior() {
        Conta contaInvalida = new Conta(LocalDate.now(), LocalDate.now().minusDays(1), BigDecimal.valueOf(100), "Teste", SituacaoConta.PENDENTE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            contaService.validarConta(contaInvalida);
        });

        assertEquals("A data de pagamento não pode ser anterior à data de vencimento.", exception.getMessage());
    }
}
