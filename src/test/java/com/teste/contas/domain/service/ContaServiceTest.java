package com.teste.contas.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.teste.contas.application.dto.ContaAtualizadaRequest;
import com.teste.contas.application.dto.ContaNovaRequest;
import com.teste.contas.domain.exception.ContaInvalidException;
import com.teste.contas.domain.exception.ContaNotFoundException;
import com.teste.contas.domain.model.Conta;
import com.teste.contas.domain.model.Situacao;
import com.teste.contas.domain.repository.ContaRepository;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private ContaNovaRequest contaNovaRequest;
    private ContaAtualizadaRequest contaAtualizadaRequest;
    private Conta conta;

    @BeforeEach
    public void setUp() {
        contaNovaRequest = new ContaNovaRequest();
        contaNovaRequest.setDescricao("Conta de luz");
        contaNovaRequest.setDataVencimento(LocalDate.of(2024, 11, 10));
        contaNovaRequest.setValor(BigDecimal.valueOf(100.50));

        contaAtualizadaRequest = new ContaAtualizadaRequest();
        contaAtualizadaRequest.setId(1L);
        contaAtualizadaRequest.setDescricao("Conta de água");
        contaAtualizadaRequest.setDataVencimento(LocalDate.of(2024, 11, 15));
        contaAtualizadaRequest.setValor(BigDecimal.valueOf(150.75));

        conta = new Conta();
        conta.setId(1L);
        conta.setDescricao("Conta de luz");
        conta.setDataVencimento(LocalDate.of(2024, 11, 10));
        conta.setValor(BigDecimal.valueOf(100.50));
    }

    @Test
    public void testCriarConta_Sucesso() {
        when(contaRepository.existeDescricaoCadastrada("Conta de luz")).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta result = contaService.criarConta(contaNovaRequest);

        assertEquals(conta.getId(), result.getId());
        assertEquals("Conta de luz", result.getDescricao());
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    public void testCriarConta_DescricaoJaCadastrada() {
        when(contaRepository.existeDescricaoCadastrada("Conta de luz")).thenReturn(true);

        ContaInvalidException exception = assertThrows(ContaInvalidException.class, () -> {
            contaService.criarConta(contaNovaRequest);
        });

        assertEquals("Descrição já criada.", exception.getMessage());
    }

    @Test
    public void testAtualizarConta_Sucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta result = contaService.atualizarConta(contaAtualizadaRequest);

        assertEquals(conta.getId(), result.getId());
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    public void testAtualizarConta_ContaNaoEncontrada() {
        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        ContaNotFoundException exception = assertThrows(ContaNotFoundException.class, () -> {
            contaService.atualizarConta(contaAtualizadaRequest);
        });

        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    public void testAtualizarSituacaoConta_Sucesso() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta result = contaService.atualizarSituacaoConta(1L, Situacao.PAGO);

        assertEquals(Situacao.PAGO, result.getSituacao());
        assertNotNull(result.getDataPagamento());
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    public void testAtualizarSituacaoConta_ContaNaoEncontrada() {
        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        ContaNotFoundException exception = assertThrows(ContaNotFoundException.class, () -> {
            contaService.atualizarSituacaoConta(1L, Situacao.PAGO);
        });

        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    public void testImportarContas_Sucesso() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data_vencimento,data_pagamento,valor,descricao,situacao\n2024-11-01,,100.50,Conta de luz,PAGO".getBytes()));

        contaService.importarContas(file);

        verify(contaRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testImportarContas_ArquivoInvalido() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Erro ao ler o arquivo."));

        ContaInvalidException exception = assertThrows(ContaInvalidException.class, () -> {
            contaService.importarContas(file);
        });

        assertEquals("Erro ao importar arquivo: Erro ao ler o arquivo.", exception.getMessage());
    }
}