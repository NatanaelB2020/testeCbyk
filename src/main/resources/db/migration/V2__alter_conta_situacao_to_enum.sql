-- Criar o tipo enum para a situação da conta
CREATE TYPE situacao_conta AS ENUM ('PENDENTE', 'PAGA');

-- Alterar a coluna 'situacao' para usar o novo tipo 'situacao_conta'
ALTER TABLE conta
    ALTER COLUMN situacao TYPE situacao_conta
    USING situacao::situacao_conta;

-- Caso queira definir um valor padrão, pode fazer isso aqui (opcional)
ALTER TABLE conta ALTER COLUMN situacao SET DEFAULT 'PENDENTE';
