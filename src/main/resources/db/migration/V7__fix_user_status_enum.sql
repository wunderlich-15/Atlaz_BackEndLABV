ALTER TABLE usuario 
DROP CONSTRAINT usuario_usuario_status_check;

ALTER TABLE usuario 
ADD CONSTRAINT usuario_usuario_status_check
CHECK (usuario_status IN ('EM_CAMPO', 'DISPONIVEL', 'DESLIGADO'));

UPDATE usuario 
SET usuario_status = 'DISPONIVEL' 
WHERE usuario_status NOT IN ('EM_CAMPO', 'DISPONIVEL', 'DESLIGADO');