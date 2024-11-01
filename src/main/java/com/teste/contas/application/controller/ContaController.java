package com.teste.contas.application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.teste.contas.application.dto.ContaAtualizadaRequest;
import com.teste.contas.application.dto.ContaNovaRequest;
import com.teste.contas.application.dto.ContaPagaRequest;
import com.teste.contas.application.dto.ListaContaPagaResponse;
import com.teste.contas.domain.model.Conta;
import com.teste.contas.domain.model.Situacao;
import com.teste.contas.domain.service.ContaService;

import jakarta.validation.Valid;
import lombok.extern.java.Log;

@Log
@RestController
@RequestMapping("/api/conta")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/v1.0")
    public ResponseEntity<Long> criarConta(@RequestBody ContaNovaRequest contaRequestDto) {
        Conta conta = contaService.criarConta(contaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(conta.getId());
    }

    @PutMapping("/v1.0")
    public ResponseEntity<Long> atualizarConta(@RequestBody ContaAtualizadaRequest contaRequestDto) {
        Conta conta = contaService.atualizarConta(contaRequestDto);
        return ResponseEntity.ok(conta.getId());
    }

    @PutMapping("/v1.0/{id}/alterarsituacao")
    public ResponseEntity<Long> atualizarSituacaoConta(@Valid @PathVariable Long id,
            @RequestParam(defaultValue = "PAGO") Situacao situacao) {
        Conta conta = contaService.atualizarSituacaoConta(id, situacao);
        return ResponseEntity.ok(conta.getId());
    }

    @GetMapping("/v1.0/contaspagas")
    public ResponseEntity<List<ListaContaPagaResponse>> obterListaContarPagas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody ContaPagaRequest contaPagaRequest) {
        List<Conta> contas = contaService.obterListaContarPagas(contaPagaRequest.getDataVencimento(),
                contaPagaRequest.getDescricao(), page, size);
        List<ListaContaPagaResponse> contasPagasDto = ListaContaPagaResponse
                .listContasParaListContaAtualizadaRequest(contas);
        return ResponseEntity.ok(contasPagasDto);
    }

    @GetMapping("/v1.0/{id}")
    public ResponseEntity<Conta> obterConta(@Valid @PathVariable Long id) {
        Conta contra = contaService.buscarContaPorId(id);
        return ResponseEntity.ok(contra);
    }

    @GetMapping("/v1.0/periodopago")
    public ResponseEntity<Double> obterTotalPeriodoPago(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        Double valorTotal = contaService.obterTotalPeriodoPago(dataInicio, dataFim);
        return ResponseEntity.ok(valorTotal);
    }

    @PostMapping("/v1.0/importar")
    public ResponseEntity<String> importarContas(@RequestParam("file") MultipartFile file) throws IOException {
        contaService.importarContas(file);
        return ResponseEntity.ok("Importação realizada com sucesso!");

    }
}
