-- 1. Adiciona a coluna (permitindo NULL inicialmente)
ALTER TABLE viatura
    ADD COLUMN tipo_abastecimento VARCHAR(50);

-- 2. Constraint com valores permitidos
ALTER TABLE viatura
    ADD CONSTRAINT viatura_tipo_abastecimento_check
    CHECK (tipo_abastecimento IN ('GASOLINA', 'ETANOL', 'DIESEL', 'GNV', 'FLEX'));

-- 3. Preenche registros antigos
UPDATE viatura
SET tipo_abastecimento = 'GASOLINA'
WHERE tipo_abastecimento IS NULL;

-- 4. Torna obrigatório
ALTER TABLE viatura
    ALTER COLUMN tipo_abastecimento SET NOT NULL;