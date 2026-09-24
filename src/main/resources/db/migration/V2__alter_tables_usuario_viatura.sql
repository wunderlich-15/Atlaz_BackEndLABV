ALTER TABLE usuario 
DROP CONSTRAINT usuario_usuario_status_check;

ALTER TABLE usuario 
ALTER COLUMN usuario_status SET DEFAULT 'DISPONIVEL';

ALTER TABLE usuario 
ADD CONSTRAINT usuario_usuario_status_check
CHECK (usuario_status IN ('EM CAMPO', 'DISPONIVEL', 'DESLIGADO'));