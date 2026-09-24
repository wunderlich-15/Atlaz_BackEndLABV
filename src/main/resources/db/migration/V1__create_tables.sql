CREATE TABLE usuario (
                         id_usuario BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(100) NOT NULL,
                         matricula VARCHAR(50) UNIQUE NOT NULL,
                         email VARCHAR(150) UNIQUE NOT NULL,
                         senha_hash VARCHAR(255) NOT NULL,
                         perfil VARCHAR(50) NOT NULL CHECK (perfil IN ('ADMIN', 'TECNICO')),
                         usuario_status VARCHAR(20) DEFAULT 'ATIVO' CHECK (usuario_status IN ('ATIVO', 'INATIVO'))
);

CREATE TABLE cidade (
                        id_cidade BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        uf VARCHAR(2) NOT NULL
);

CREATE TABLE modelo (
                        id_modelo BIGSERIAL PRIMARY KEY,
                        nome_modelo VARCHAR(100) NOT NULL,
                        nome_marca VARCHAR(100) NOT NULL
);

CREATE TABLE viatura (
                         id_viatura BIGSERIAL PRIMARY KEY,
                         prefixo VARCHAR(50) UNIQUE NOT NULL,
                         tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('UTILITARIO', 'PASSEIO')),
                         viatura_status VARCHAR(20) DEFAULT 'ATIVO' CHECK (viatura_status IN ('ATIVO', 'INATIVO', 'MANUTENCAO')),
                         id_modelo BIGINT NOT NULL,
                         CONSTRAINT fk_viatura_modelo FOREIGN KEY (id_modelo)
                             REFERENCES modelo (id_modelo) ON DELETE RESTRICT
);

CREATE TABLE ordem_servico (
                               id_os BIGSERIAL PRIMARY KEY,
--                                numero_os VARCHAR(50) UNIQUE NOT NULL,
                               tipo_servico VARCHAR(100) NOT NULL,
                               local_destino VARCHAR(150),
                               justificativa VARCHAR(255),
                               requisitante VARCHAR(100),
                               km_saida NUMERIC(10, 2) NOT NULL,
                               km_chegada NUMERIC(10, 2),
                               data_saida TIMESTAMP NOT NULL,
                               data_retorno TIMESTAMP,
                               id_usuario BIGINT NOT NULL,
                               id_viatura BIGINT NOT NULL,
                               CONSTRAINT fk_os_usuario FOREIGN KEY (id_usuario)
                                   REFERENCES usuario (id_usuario) ON DELETE RESTRICT,
                               CONSTRAINT fk_os_viatura FOREIGN KEY (id_viatura)
                                   REFERENCES viatura (id_viatura) ON DELETE RESTRICT
);

CREATE TABLE abastecimento (
                               id_abastecimento BIGSERIAL PRIMARY KEY,
                               data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               km_atual NUMERIC(10, 2),
                               litros NUMERIC(10, 2) NOT NULL,
                               valor_total NUMERIC(10, 2) NOT NULL,
                               tipo_combustivel VARCHAR(50) CHECK (tipo_combustivel IN ('GASOLINA', 'ETANOL', 'DIESEL', 'GNV')),
                               numero_nota_fiscal VARCHAR(100) NOT NULL,
                               observacao_estado TEXT,
                               id_usuario BIGINT NOT NULL,
                               id_viatura BIGINT NOT NULL,
                               id_cidade BIGINT NOT NULL,
                               id_os BIGINT,
                               CONSTRAINT fk_abast_usuario FOREIGN KEY (id_usuario)
                                   REFERENCES usuario (id_usuario) ON DELETE RESTRICT,
                               CONSTRAINT fk_abast_viatura FOREIGN KEY (id_viatura)
                                   REFERENCES viatura (id_viatura) ON DELETE RESTRICT,
                               CONSTRAINT fk_abast_cidade FOREIGN KEY (id_cidade)
                                   REFERENCES cidade (id_cidade) ON DELETE RESTRICT,
                               CONSTRAINT fk_abast_os FOREIGN KEY (id_os)
                                   REFERENCES ordem_servico (id_os) ON DELETE SET NULL
);

CREATE INDEX idx_abastecimento_viatura ON abastecimento(id_viatura);
CREATE INDEX idx_abastecimento_data ON abastecimento(data_hora);
CREATE INDEX idx_os_viatura ON ordem_servico(id_viatura);
