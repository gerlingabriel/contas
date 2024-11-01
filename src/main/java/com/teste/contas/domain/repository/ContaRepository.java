package com.teste.contas.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teste.contas.domain.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM conta c WHERE c.descricao = :descricao", nativeQuery = true)
    boolean existeDescricaoCadastrada(@Param("descricao") String descricao);

    @Query("SELECT c FROM Conta c " +
            "WHERE c.situacao = 'PAGO' " + // Filtra contas pagas
            "AND c.dataVencimento = :dataVencimento " + // Filtro por data de vencimento
            "AND LOWER(c.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))") // Filtro por descrição
    List<Conta> buscarListaContasPagasPorFiltroDataVencimentoEDescricao(String descricao, LocalDate dataVencimento,
            Pageable pageable);

    @Query(value = "SELECT COALESCE(SUM(c.valor), 0) FROM conta c " +
            "WHERE c.data_pagamento BETWEEN :dataInicio AND :dataFim " + // Filtro do periodo selcionado
            "AND c.situacao = 'PAGO'", nativeQuery = true) //Situacao necessario para selecionar as contas
    Double obterSomaContasPagasPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    Conta findByDescricao(String descricao);

}
