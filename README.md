# Controle Bancário POO (Gradle/Java)

![Java 17](https://img.shields.io/badge/Java-17-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.x-green)
![Lombok](https://img.shields.io/badge/Lombok-enabled-orange)
![Status](https://img.shields.io/badge/status-Bootcamp%20DIO%20challenge-success)

> Projeto de estudo para o **Bootcamp NTT DATA - Java e IA para Iniciantes (DIO)**.  
> Autor: **Robson** — GitHub: https://github.com/robsonosbor

---

## ✨ Visão Geral

Aplicação **CLI** para controle de transações financeiras utilizando conceitos de **POO** e modelagem por **carteiras**. O projeto simula uma conta corrente e uma carteira de investimentos, permitindo operações como:

- Criar conta com chaves **PIX** e depósito inicial  
- **Depósito**, **saque** e **transferência** entre contas
- **Abrir investimento** e **aportar** a partir da conta
- **Rendimentos** simulados por percentual
- **Histórico** de transações com auditoria (UUID, descrição e timestamp)
- Tratamento de **exceções** de domínio (sem saldo, chaves PIX duplicadas, itens não encontrados etc.)

> Observação: o app é didático e mantém dados **em memória** (sem banco de dados).

---

## 🧱 Arquitetura & Conceitos

- **Domain**
  - `Wallet` (abstração de carteira, saldo por _centavos_ representado como lista de unidades `Money`)
  - `AccountWallet` (carteira de conta corrente + chaves PIX)
  - `InvestmentWallet` (carteira de investimento vinculada a uma conta)
  - `Investment` (produto com taxa e aporte inicial)
  - `Money` e `MoneyAudit` (histórico imutável de transações)
  - `BankService` (`ACCOUNT`, `INVESTMENT`)

- **Repositories (in-memory)**
  - `AccountRepository` — cria contas, depósito/saque/transferência, busca por PIX, histórico
  - `InvestmentRepository` — cria investimentos, abre carteiras, aplica rendimentos, busca por conta

- **Exceptions**
  - `AccountNotFoundException`, `WalletNotFoundException`, `InvestmentNotFoundException`
  - `NoFundsEnoughException`, `PixInUseException`, `AccountWithInvestmentException`

- **CLI**
  - `Main` — menu simples via `Scanner`, leitura de opções e chamada aos repositórios.

> A estratégia de saldo por **lista de `Money`** permite auditar o valor pelo tamanho da coleção. Cada unidade representa **1 centavo** com um `MoneyAudit` compartilhado — útil para **impressão do histórico por timestamp**.

---

## 📁 Estrutura do Projeto

```
controle-bancario-poo/
├── app/
│   ├── build.gradle.kts
│   └── src/main/java/br/com/dio/
│       ├── Main.java
│       ├── exception/...
│       ├── model/...
│       └── repository/...
├── gradle/
│   └── libs.versions.toml
├── gradlew / gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Como executar

### Pré-requisitos
- **Java 17+** (toolchain configurada no Gradle)
- **Gradle Wrapper** (já incluso: `./gradlew`)

> O projeto utiliza **Lombok**. Em IDEs, habilite o plugin do Lombok se necessário.

### Build e Run

```bash
# dentro da pasta do projeto
cd controle-bancario-poo

# listar tarefas
./gradlew tasks

# compilar
./gradlew :app:build

# executar (classe principal: br.com.dio.Main)
./gradlew :app:run
```

No Windows, use `gradlew.bat`:

```bat
gradlew.bat :app:run
```

---

## 💻 Uso (CLI)

O menu interativo solicita dados pelo console. Exemplos comuns:

- **Criar conta**: informe chaves PIX separadas por `;` e valor inicial (em centavos).
- **Depositar**: informe PIX da conta e valor (em centavos).
- **Sacar**: informe PIX e valor; valida saldo.
- **Transferir**: informe PIX de origem, PIX de destino e valor.
- **Criar investimento**: informe taxa (%) e aporte inicial.
- **Abrir carteira de investimento**: escolha conta (PIX) e valor a aplicar.
- **Aplicar rendimento**: informe percentual; o sistema gera novas unidades de `Money`.
- **Histórico**: agrupa registros por segundo (`OffsetDateTime.truncatedTo(SECONDS)`), mostra **UUID**, **descrição** e **valor** por timestamp.

> Observação: como os valores são em centavos, `1234` representa **R$ 12,34** no console.

---

## ✅ Requisitos do Desafio (DIO)

- POO: classes, herança, composição e polimorfismo (`Wallet` ↔ `AccountWallet`/`InvestmentWallet`)
- Encapsulamento e uso de **records** (`Investment`, `MoneyAudit`)
- Tratamento de exceções específicas
- Projeto **multi-módulo Gradle** com **toolchain Java 17** e **wrapper**
- Interface de linha de comando funcional

---

## 🧪 Testes

O módulo de testes não está habilitado no momento. Sugestões:
- Criar casos de unidade para `AccountRepository` e `InvestmentRepository`
- Testar regras de negócio (ex.: saldo insuficiente, PIX duplicado, histórico por timestamp)
- Usar **JUnit Jupiter 5** (declaração já prevista em `libs.versions.toml`)

---

## 📦 Roadmap de Melhorias

- Persistência com banco (ex.: H2 + JPA) ou arquivos
- Validação e formatação de PIX
- Camada de serviço separada dos repositórios
- Mais operações no investimento (resgate parcial/total, taxas de IOF/IR simuladas)
- Testes automatizados e CI (GitHub Actions)
- Internacionalização (pt-BR / en-US)

---

## 🧑‍💻 Autor

**Robson** — https://github.com/robsonosbor

---

## 📄 Licença

Defina uma licença para o repositório (recomendado: MIT). Exemplo de cabeçalho:

```
MIT License © 2025 Robson
```

---

## 🔎 Notas técnicas

- O saldo é calculado por `Wallet#getFunds()` somando a quantidade de itens na coleção `money` (1 item = 1 centavo).
- O histórico de transações é obtido com `Wallet#getFinancialTransactions()` e agrupado no repositório da conta por segundo, o que facilita a leitura no console.
- `CommonsRepository#generateMoney` centraliza a criação de unidades `Money` com o mesmo `MoneyAudit` (UUID/descrição/serviço/timestamp).
- `AccountRepository` evita chaves PIX duplicadas antes de criar uma conta.
- `InvestmentWallet#updateAmount(percent)` simula rendimento a partir do percentual informado.

---

_Última atualização: 2025-08-21_
