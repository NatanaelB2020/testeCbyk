package com.example.desafio.service;

import com.example.desafio.entidade.Conta;
import com.example.desafio.entidade.SituacaoConta;
import com.example.desafio.repository.ContaRepository;

import com.example.desafio.util.CsvImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    // Construtor
    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    // Método para criar uma nova conta
    public Conta createConta(Conta conta) {
        return contaRepository.save(conta);
    }

    // Método para criar várias contas
    public List<Conta> createContas(List<Conta> contas) {
        return contaRepository.saveAll(contas);
    }

    // Método para atualizar uma conta existente
    public Conta atualizarConta(Long id, Conta contaAtualizada) {
        return contaRepository.findById(id)
                .map(contaExistente -> {
                    Optional.ofNullable(contaAtualizada.getDataVencimento())
                            .ifPresent(contaExistente::setDataVencimento);
                    Optional.ofNullable(contaAtualizada.getDataPagamento())
                            .ifPresent(contaExistente::setDataPagamento);
                    Optional.ofNullable(contaAtualizada.getValor())
                            .ifPresent(contaExistente::setValor);
                    Optional.ofNullable(contaAtualizada.getDescricao())
                            .ifPresent(contaExistente::setDescricao);
                    Optional.ofNullable(contaAtualizada.getSituacao())
                            .ifPresent(contaExistente::setSituacao);

                    return contaRepository.save(contaExistente);
                }).orElse(null);
    }

    // Método para alterar a situação de uma conta
    public void alterarSituacao(Long id, SituacaoConta novaSituacao) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com id: " + id));
        conta.setSituacao(novaSituacao);
        contaRepository.save(conta);
    }

    // Método para listar contas com paginação
    public Page<Conta> listarContas(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public Optional<Conta> buscarPorId(Long id) {
        return contaRepository.findById(id);
    }



    public Page<Conta> buscarComFiltro(LocalDate dataInicio, LocalDate dataFim, String descricao, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contaRepository.findAll((Specification<Conta>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dataInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), dataInicio));
            }

            if (dataFim != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), dataFim));
            }

            if (descricao != null && !descricao.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("descricao"), "%" + descricao + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    // Método para obter o valor total pago
    public BigDecimal obterValorTotalPago(LocalDate start, LocalDate end) {
        return contaRepository.calcularTotalPago(SituacaoConta.PAGA, start, end);
    }


    private final CsvImporter csvImporter = new CsvImporter();

    public void importarContasFromCSV(MultipartFile file) {
        List<Conta> contas = csvImporter.importarContas(file);

        for (Conta conta : contas) {
            contaRepository.save(conta);
        }
    }


    public void validarConta(Conta conta) {
        if (conta.getValor() == null || conta.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor da conta deve ser positivo.");
        }
        if (conta.getDataVencimento() != null && conta.getDataPagamento() != null
                && conta.getDataPagamento().isBefore(conta.getDataVencimento())) {
            throw new IllegalArgumentException("A data de pagamento não pode ser anterior à data de vencimento.");
        }
    }
}

