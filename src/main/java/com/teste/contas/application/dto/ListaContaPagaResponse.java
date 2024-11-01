package com.teste.contas.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.teste.contas.domain.model.Conta;
import com.teste.contas.domain.model.Situacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListaContaPagaResponse {

    private LocalDate dataVencimento;
    private BigDecimal valor;
    private String descricao;
    private Situacao situacao;

    public static List<ListaContaPagaResponse> listContasParaListContaAtualizadaRequest(List<Conta> contas) {
        return contas
                .stream()
                .map(conta -> {
                    ListaContaPagaResponse request = new ListaContaPagaResponse();
                    request.setDataVencimento(conta.getDataVencimento());
                    request.setValor(conta.getValor());
                    request.setDescricao(conta.getDescricao());
                    request.setSituacao(conta.getSituacao());
                    return request;
                })
                .collect(Collectors.toList());
    }

}
