-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema eventosdb
-- -----------------------------------------------------

CREATE SCHEMA IF NOT EXISTS eventosdb DEFAULT CHARACTER SET utf8 ;
USE eventosdb ;

-- -----------------------------------------------------
-- Table eventosdb.usuario
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS eventosdb.usuario (
  usuario_id INT NOT NULL AUTO_INCREMENT,
  usuario_nome VARCHAR(255) NOT NULL,
  usuario_idade INT NOT NULL,
  usuario_cpf CHAR(11) NOT NULL,
  usuario_email VARCHAR(255) NOT NULL,
  usuario_senha VARCHAR(30) NOT NULL,
  usuario_tipo ENUM("comum", "admin", "organizador") NOT NULL,
  PRIMARY KEY (usuario_id))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table eventosdb.evento
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS eventosdb.evento (
  evento_id INT NOT NULL AUTO_INCREMENT,
  evento_titulo VARCHAR(255) NOT NULL,
  evento_descricao VARCHAR(255) NOT NULL,
  evento_tipo VARCHAR(45) NOT NULL,
  evento_data DATE NOT NULL,
  usuario_usuario_id INT NOT NULL,
  PRIMARY KEY (evento_id),
  INDEX fk_evento_usuario1_idx (usuario_usuario_id ASC) VISIBLE,
  CONSTRAINT fk_evento_usuario1
    FOREIGN KEY (usuario_usuario_id)
    REFERENCES eventosdb.usuario (usuario_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table eventosdb.endereco
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS eventosdb.endereco (
  endereco_id INT NOT NULL AUTO_INCREMENT,
  endereco_estado VARCHAR(45) NOT NULL,
  endereco_cidade VARCHAR(45) NOT NULL,
  endereco_rua VARCHAR(45) NOT NULL,
  endereco_numero VARCHAR(45) NOT NULL,
  endereco_lotacao INT NOT NULL,
  evento_evento_id INT NOT NULL,
  PRIMARY KEY (endereco_id),
  INDEX fk_endereco_evento1_idx (evento_evento_id ASC) VISIBLE,
  CONSTRAINT fk_endereco_evento1
    FOREIGN KEY (evento_evento_id)
    REFERENCES eventosdb.evento (evento_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table eventosdb.recurso
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS eventosdb.recurso (
  recursos_id INT NOT NULL AUTO_INCREMENT,
  recursos_nome VARCHAR(255) NOT NULL,
  recursos_qtd INT NOT NULL,
  recursos_descricao VARCHAR(255) NOT NULL,
  evento_evento_id INT NOT NULL,
  PRIMARY KEY (recursos_id),
  INDEX fk_recursos_evento_idx (evento_evento_id ASC) VISIBLE,
  CONSTRAINT fk_recursos_evento
    FOREIGN KEY (evento_evento_id)
    REFERENCES eventosdb.evento (evento_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table eventosdb.atracao
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS eventosdb.atracao (
  atracoes_id INT NOT NULL AUTO_INCREMENT,
  atracoes_nome VARCHAR(45) NOT NULL,
  atracoes_tipo VARCHAR(45) NOT NULL,
  atracoes_horario VARCHAR(45) NOT NULL,
  evento_evento_id INT NOT NULL,
  PRIMARY KEY (atracoes_id),
  INDEX fk_atracoes_evento1_idx (evento_evento_id ASC) VISIBLE,
  CONSTRAINT fk_atracoes_evento1
    FOREIGN KEY (evento_evento_id)
    REFERENCES eventosdb.evento (evento_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;