package com.teste.contas.domain.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.teste.contas.application.dto.ContaAtualizadaRequest;
import com.teste.contas.application.dto.ContaNovaRequest;
import com.teste.contas.domain.exception.ContaInvalidException;
import com.teste.contas.domain.exception.ContaNotFoundException;
import com.teste.contas.domain.model.Conta;
import com.teste.contas.domain.model.Situacao;
import com.teste.contas.domain.repository.ContaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Transactional
    public Conta criarConta(ContaNovaRequest contaRequestDto) {
        validarContaParaCadastrar(contaRequestDto);
        Conta conta = new Conta();
        conta.setDescricao(contaRequestDto.getDescricao());
        conta.setDataVencimento(contaRequestDto.getDataVencimento());
        conta.setValor(contaRequestDto.getValor());
        return contaRepository.save(conta);
    }

    @Transactional
    public Conta atualizarConta(ContaAtualizadaRequest contaRequestDto) {
        validarContaParaAtualizar(contaRequestDto);
        Conta conta = buscarContaPorId(contaRequestDto.getId());
        verificarDescricaoParaAtualizacao(conta, contaRequestDto);
        conta.setDescricao(contaRequestDto.getDescricao());
        conta.setDataVencimento(contaRequestDto.getDataVencimento());
        conta.setValor(contaRequestDto.getValor());
        return contaRepository.save(conta);
    }

    private void verificarDescricaoParaAtualizacao(Conta conta, ContaAtualizadaRequest contaRequestDto) {
        Conta buscaContaPorDescricao = contaRepository.findByDescricao(contaRequestDto.getDescricao());
        if (buscaContaPorDescricao != null && buscaContaPorDescricao.getId() != conta.getId()
                && contaRequestDto.getDescricao().equalsIgnoreCase(buscaContaPorDescricao.getDescricao()))
            throw new ContaInvalidException("Descrição já criada.");
    }

    public Conta buscarContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ContaNotFoundException("Conta não encontrada."));
    }

    private void validarContaParaCadastrar(ContaNovaRequest contaRequestDto) {
        if (contaRequestDto == null) {
            throw new ContaInvalidException("Dados de conta não fornecidos.");
        }
        verificarDescricaoExistente(contaRequestDto.getDescricao());
    }

    private void validarContaParaAtualizar(ContaAtualizadaRequest contaRequestDto) {
        if (contaRequestDto == null) {
            throw new ContaInvalidException("Dados de conta não fornecidos.");
        }
        validarIdNull(contaRequestDto.getId());
        validarDescricao(contaRequestDto.getDescricao());
    }

    private void verificarDescricaoExistente(String descricao) {
        if (contaRepository.existeDescricaoCadastrada(descricao))
            throw new ContaInvalidException("Descrição já criada.");
    }

    private void validarDescricao(String descricao) {
        if (descricao == null)
            throw new ContaInvalidException("Campo descricao está nulla ou vazia.");
    }

    @Transactional
    public Conta atualizarSituacaoConta(Long id, Situacao situacao) {
        validarSituacaoConta(id, situacao);
        validarSituacaoNaoNula(situacao);
        Conta conta = buscarContaPorId(id);
        conta.setSituacao(situacao);
        atualizarDataPagamento(situacao, conta);
        return contaRepository.save(conta);
    }

    private void atualizarDataPagamento(Situacao situacao, Conta conta) {
        if (situacao.equals(Situacao.PAGO))
            conta.setDataPagamento(LocalDate.now());
        else
            conta.setDataPagamento(null);
    }

    private void validarSituacaoConta(@Valid Long id, Situacao situacao) {
        validarIdNull(id);
        validarSituacaoNaoNula(situacao);
    }

    private void validarSituacaoNaoNula(Situacao situacao) {
        if (situacao == null)
            throw new ContaInvalidException("Situação da conta não fornecida ou vazia.");
    }

    private void validarIdNull(Long id) {
        if (id == null)
            throw new ContaInvalidException("ID da conta não fornecido.");
    }

    public List<Conta> obterListaContarPagas(LocalDate dataVencimento, String descricao, int page, int size) {
        validarDescricao(descricao);
        validarDataVencimento(dataVencimento);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataVencimento").descending());
        return contaRepository.buscarListaContasPagasPorFiltroDataVencimentoEDescricao(descricao, dataVencimento,
                pageable);
    }

    private void validarDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null)
            throw new ContaInvalidException("Data vencimento está nula ou vazia.");
        ;
    }

    public Double obterTotalPeriodoPago(LocalDate dataInicio, LocalDate dataFim) {
        validarDataVencimento(dataInicio);
        validarDataVencimento(dataFim);
        return contaRepository.obterSomaContasPagasPorPeriodo(dataInicio, dataFim);
    }

    @Transactional
    public void importarContas(MultipartFile file) throws IOException {
        List<Conta> contas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linha;
            reader.readLine(); // Pula o cabeçalho do CSV
            while ((linha = reader.readLine()) != null) {
                linha = linha.startsWith("\"") && linha.endsWith("\"") ? linha.substring(1, linha.length() - 1) : linha;
                String[] dados = linha.split(",");
                Conta conta = new Conta();
                conta.setDataVencimento(LocalDate.parse(dados[0].trim()));
                conta.setDataPagamento(dados[1].trim().isEmpty() ? null : LocalDate.parse(dados[1].trim()));
                conta.setValor(new BigDecimal(dados[2].trim()));
                conta.setDescricao(dados[3].trim());
                conta.setSituacao(Situacao.valueOf(dados[4].trim().toUpperCase()));
                contas.add(conta);
            }
        } catch (Exception e) {
            throw new ContaInvalidException("Erro ao importar arquivo: " + e.getMessage());
        }

        contaRepository.saveAll(contas);
    }
}
