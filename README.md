# 📊 Banking App – Arquiteturas de Software

Este repositório contém a implementação de uma **mesma aplicação bancária** em três diferentes arquiteturas de software (**Monolito, Microsserviços e Serverless**) como parte do Trabalho de Conclusão de Curso (TCC).

O objetivo é **comparar desempenho, escalabilidade e manutenção** entre os diferentes estilos arquiteturais.

---

## 🏗 Arquiteturas implementadas

### 🔹 1. Monolítica
- Um único projeto Spring Boot centralizado.
- Contém os módulos de **Account, Transaction, Payment e Owner**.
- Estrutura simples, mas com menor flexibilidade para evolução.

📂 Diretório: `/banking-app-monolith`
- **groupId:** `com.tcc`
- **artifactId:** `banking-app-monolith`

---

### 🔹 2. Microsserviços
- Cada domínio do sistema foi separado em um serviço independente.
- Comunicação entre serviços pode usar **REST** e **mensageria**.
- Inclui API Gateway e Service Discovery para orquestração.

📂 Diretório: `/banking-app-microservices`  
Composto pelos seguintes serviços:
- `account-service`
- `transaction-service`
- `payment-service`
- `owner-service`
- `gateway-service`
- `discovery-service`

**groupId base:** `com.tcc.microservices`

---

### 🔹 3. Serverless
- Implementação baseada em **funções serverless** (Spring Cloud Function).
- Ideal para **eventos pontuais** e **escalabilidade automática**.
- Serviços desacoplados em funções que processam eventos de Conta, Transação e Pagamento.

📂 Diretório: `/banking-app-serverless`
- **groupId:** `com.tcc`
- **artifactId:** `banking-app-serverless`

---

## 📂 Estrutura do Repositório

/banking-app-monolith
- Projeto monolítico (Spring Boot único)

/banking-app-microservices
- /account-service
- /transaction-service
- /payment-service
- /owner-service
- /gateway-service
- /discovery-service

/banking-app-serverless 
- Projeto serverless


---

## 📖 Objetivo do TCC
- Comparar diferentes **arquiteturas de software** aplicadas ao mesmo domínio (aplicação bancária).
- Avaliar **métricas de desempenho**, **consistência**, **manutenibilidade** e **custos operacionais**.
- Utilizar ferramentas de monitoramento como **Grafana, Prometheus, Datadog** e realizar **testes de carga com JMeter**.

---

## 🚀 Tecnologias Utilizadas
- **Java 21** + **Spring Boot**
- **Spring Cloud** (para microsserviços)
- **Spring Cloud Function** (para serverless)
- **Docker** (deploy)
- **Mensageria:** Em análise
- **Banco de Dados:** PostgreSQL
- **Monitoramento:** Prometheus, Grafana, Datadog*

---

## ✍️ Autores
Gabriel Alves – Trabalho de Conclusão de Curso <br>
Giovanni Marques - Trabalho de Conclusão de Curso


