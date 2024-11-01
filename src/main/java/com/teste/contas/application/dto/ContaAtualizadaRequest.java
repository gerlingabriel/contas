package com.teste.contas.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaAtualizadaRequest {

    @NotBlank
    private Long id;
    @NotNull
    private LocalDate dataVencimento;
    @NotNull
    @Positive
    private BigDecimal valor;
    @NotBlank
    private String descricao;
    
}
