-- 1. Atualiza dados existentes (IMPORTANTE)
UPDATE viatura
SET viatura_status = 'EM_USO'
WHERE viatura_status = 'EM USO';

-- 2. Remove constraint antiga
ALTER TABLE viatura
DROP CONSTRAINT viatura_viatura_status_check;

-- 3. Cria nova constraint
ALTER TABLE viatura
    ADD CONSTRAINT viatura_viatura_status_check
        CHECK (viatura_status IN ('DISPONIVEL', 'MANUTENCAO', 'EM_USO', 'DESATIVADA'));