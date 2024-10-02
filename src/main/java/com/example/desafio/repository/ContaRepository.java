package com.example.desafio.repository;

import com.example.desafio.entidade.Conta;
import com.example.desafio.entidade.SituacaoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long>, JpaSpecificationExecutor<Conta> {
    @Query("SELECT SUM(c.valor) FROM Conta c WHERE c.dataPagamento IS NOT NULL " +
            "AND c.situacao = :situacao " +
            "AND c.dataPagamento BETWEEN :start AND :end")
    BigDecimal calcularTotalPago(@Param("situacao") SituacaoConta situacao,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);
}
