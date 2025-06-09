# Gestor de Eventos (POO)

Este é um projeto de um Gestor de Eventos desenvolvido em Java, utilizando Programação Orientada a Objetos e um banco de dados MySQL rodando em um container Docker.

## Pré-requisitos

Antes de começar, garanta que você tenha instalado:

* **Java Development Kit (JDK)**
* **Docker** e **Docker Compose**
* O **MySQL Connector/J** (arquivo `.jar`). Você pode baixá-lo no [site oficial do MySQL](https://dev.mysql.com/downloads/connector/j/).

---

## Como Executar

Siga os passos abaixo para rodar o projeto na sua máquina.

### 1. Clonar o Repositório

Primeiro, clone este repositório para o seu computador.

```bash
git clone (https://github.com/Thiagohfc/POO_Gestor_de_Eventos.git)
```
### 2. Acessar a Pasta do Projeto

Navegue até a pasta raiz do projeto que você acabou de clonar.

```bash
cd POO_Gestor_de_Eventos
```

### 3. Subir o Banco de Dados

Use o Docker Compose para criar e iniciar o container com o banco de dados MySQL. O -d faz com que ele rode em segundo plano.

```bash
docker compose up -d
```
### 4. Acessar a Pasta do Código Fonte

Agora, entre na pasta onde está o arquivo principal da aplicação.

```bash
cd src/main
```

### 5. Compilar e Executar o Java

Para compilar e executar, você precisa incluir o MySQL Connector no classpath.

Agora só compilar e executar a classe "Main.java".

Sistema já deve rodar de forma correta.
