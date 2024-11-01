package com.teste.contas.application.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaPagaRequest {
    private String descricao;
    private LocalDate dataVencimento;

}
