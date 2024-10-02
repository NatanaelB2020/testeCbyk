package com.example.desafio.controller;

import com.example.desafio.entidade.Conta;
import com.example.desafio.entidade.SituacaoConta;
import com.example.desafio.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    @Autowired
    private ContaService contaService;

    @Autowired
    private PagedResourcesAssembler<Conta> pagedResourcesAssembler;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/teste")
    public String hello() {
        return "Aplicação está rodando!";
    }

    // Cria uma nova conta
    @PostMapping("/create")
    public ResponseEntity<Conta> createConta(@RequestBody Conta conta) {
        Conta novaConta = contaService.createConta(conta);
        return ResponseEntity.ok(novaConta);
    }

    // Cria várias contas
    @PostMapping("/create/contas")
    public ResponseEntity<List<Conta>> createContas(@RequestBody List<Conta> contas) {
        List<Conta> novasContas = contaService.createContas(contas);
        return ResponseEntity.ok(novasContas);
    }

    // Atualiza uma conta existente
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Conta> atualizarConta(@PathVariable Long id, @RequestBody Conta conta) {
        Conta contaAtualizada = contaService.atualizarConta(id, conta);
        if (contaAtualizada != null) {
            return ResponseEntity.ok(contaAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Altera a situação da conta
    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Void> alterarSituacao(@PathVariable Long id, @RequestParam SituacaoConta situacao) {
        contaService.alterarSituacao(id, situacao);
        return ResponseEntity.noContent().build();
    }

    // Lista as contas com paginação
    @GetMapping("/lista")
    public Page<Conta> listarContas(Pageable pageable) {
        return contaService.listarContas(pageable);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Conta> buscarPorId(@PathVariable Long id) {
        Optional<Conta> conta = contaService.buscarPorId(id);

        return conta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Filtro para buscar contas por datas e descrição
    @GetMapping("/filtro")
    public ResponseEntity<Page<Conta>> buscarContas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "0") int page,  // Página inicial
            @RequestParam(defaultValue = "10") int size) {  // Tamanho da página

        Page<Conta> contas = contaService.buscarComFiltro(dataInicio, dataFim, descricao, page, size);
        return ResponseEntity.ok(contas);
    }

    // Obtém uma conta pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Conta>> obterContaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    // Obtém o valor total pago em um intervalo de datas
    @GetMapping("/total-pago")
    public BigDecimal obterValorTotalPago(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return contaService.obterValorTotalPago(startDate, endDate);
    }


    // Importa contas de um arquivo CSV
    @PostMapping("/importar")
    public ResponseEntity<Void> importarContas(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Retorna erro se o arquivo estiver vazio
        }
        try {
            contaService.importarContasFromCSV(file);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
