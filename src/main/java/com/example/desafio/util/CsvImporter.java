package com.example.desafio.util;


import com.example.desafio.entidade.Conta;
import com.example.desafio.entidade.SituacaoConta;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class CsvImporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Agora o método recebe MultipartFile ao invés de String
    public List<Conta> importarContas(MultipartFile file) {
        List<Conta> contas = new ArrayList<>();

        // Usa o InputStream do MultipartFile ao invés de FileReader
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                // Ignora o cabeçalho
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Pula a primeira linha
                }

                // Divide os dados
                String[] values = line.split(",");

                // Pega as datas e faz o parse
                LocalDate dataPagamento = parseDate(values[0]);
                LocalDate dataVencimento = parseDate(values[1]);
                String descricao = values[2].trim();
                String situacao = values[3].trim();
                BigDecimal valor = new BigDecimal(values[4].trim());

                // Lógica adicional para processar os dados...
                // Exemplo: cria e adiciona uma nova Conta à lista de contas
                Conta conta = new Conta();
                conta.setDataPagamento(dataPagamento);
                conta.setDataVencimento(dataVencimento);
                conta.setDescricao(descricao);
                conta.setSituacao(SituacaoConta.valueOf(situacao));
                conta.setValor(valor);

                contas.add(conta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contas;
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null; // Retorna null se a data estiver vazia
        }
        return LocalDate.parse(dateString, DATE_FORMAT);
    }
}