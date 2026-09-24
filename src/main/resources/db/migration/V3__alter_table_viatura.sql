-- 1. Remove a constraint antiga
ALTER TABLE viatura 
DROP CONSTRAINT viatura_viatura_status_check;

-- 2. Altera o valor padrão
ALTER TABLE viatura 
ALTER COLUMN viatura_status SET DEFAULT 'DISPONIVEL';

-- 3. Cria a nova constraint
ALTER TABLE viatura 
ADD CONSTRAINT viatura_viatura_status_check
CHECK (viatura_status IN ('DISPONIVEL', 'MANUTENCAO', 'EM USO', 'DESATIVADA'));