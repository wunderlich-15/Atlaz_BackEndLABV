-- =========================================
-- TABELA DE CNH DOS USUARIOS
-- =========================================

CREATE TABLE usuario_cnh (
                             id_usuario BIGINT NOT NULL,
                             tipo_cnh VARCHAR(2) NOT NULL,

                             PRIMARY KEY (id_usuario, tipo_cnh),

                             CONSTRAINT fk_usuario_cnh
                                 FOREIGN KEY (id_usuario)
                                     REFERENCES usuario(id_usuario)
                                     ON DELETE CASCADE,

                             CONSTRAINT usuario_cnh_tipo_check
                                 CHECK (tipo_cnh IN ('A', 'B', 'C', 'D', 'E'))
);

-- =========================================
-- NOVA COLUNA EM VIATURA
-- =========================================

ALTER TABLE viatura
    ADD COLUMN tipo_cnh_necessaria VARCHAR(2);

-- =========================================
-- CONSTRAINT DOS TIPOS VALIDOS
-- =========================================

ALTER TABLE viatura
    ADD CONSTRAINT viatura_tipo_cnh_check
        CHECK (tipo_cnh_necessaria IN ('A', 'B', 'C', 'D', 'E'));

-- =========================================
-- PREENCHER DADOS ANTIGOS
-- =========================================

UPDATE viatura
SET tipo_cnh_necessaria = 'B'
WHERE tipo_cnh_necessaria IS NULL;

-- =========================================
-- TORNAR O CAMPO OBRIGATORIO
-- =========================================

ALTER TABLE viatura
    ALTER COLUMN tipo_cnh_necessaria SET NOT NULL;