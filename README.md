# 🎓 TRILHA+ — Plataforma Inteligente de Aprendizagem

> **Projeto Acadêmico — Análise e Desenvolvimento de Sistemas**  
> **Centro Universitário Maurício de Nassau (UNINASSAU) — Caruaru/PE**  
> **Disciplina:** Laboratório de Empreendimentos Inovadores | 4º Período  
> **Orientador:** Professor Antonio

O **TRILHA+** é uma solução educacional completa desenvolvida para auxiliar estudantes universitários no planejamento, organização e acompanhamento contínuo dos estudos. A plataforma reúne em um único ecossistema: **gestão de matérias e tópicos, materiais didáticos, tutoria inteligente com IA Generativa gratuita (Groq Cloud / Llama 3.3 70B), simulados avaliativos dinâmicos com proteção anti-fraude e diagnóstico analítico de desempenho acadêmico**.

---

## 👥 Equipe do Projeto

| Integrante | Matrícula | Responsabilidade Principal |
| :--- | :---: | :--- |
| **Pedro Francisco Alves Neto** | **01864946** | **Arquitetura, Desenvolvimento e Testes do Back-end** |
| **Allan Victor Morais De Lima** | **01813117** | **Desenvolvimento do Back-end e Integrações** |
| Gabriel Henrique da Silva | 01777141 | Front-end / Mobile (React Native) |
| Vinicius Santos Cansanção | 01813852 | Front-end / Mobile (React Native) |
| Luiz Henrique Manoel da Silva | 01777463 | Documentação e Gestão do Projeto |

---

## 🏗️ Arquitetura e Stack Tecnológica

O projeto foi concebido em uma arquitetura moderna desacoplada (API First):

```
┌─────────────────────────────────┐
│     Aplicativo Mobile (Front)   │
│       React Native / Expo       │
└────────────────┬────────────────┘
                 │ HTTP REST / JSON (CORS Ativo)
                 ▼
┌─────────────────────────────────┐
│      Back-end RESTful API       │
│   Java 21 LTS + Spring Boot 4   │
│   Spring Security + Bearer JWT  │
└───────┬─────────────────┬───────┘
        │                 │
        ▼                 ▼
┌──────────────┐   ┌──────────────┐
│ Banco MySQL  │   │  Groq Cloud  │
│  Relacional  │   │  Llama 3.3   │
│   (JPA/ORM)  │   │  (IA Grátis) │
└──────────────┘   └──────────────┘
```

### ⚙️ Back-end (100% Concluído e Homologado)
- **Linguagem & Framework:** Java 21 (LTS) e Spring Boot 4.
- **Banco de Dados:** MySQL 8.0 com Spring Data JPA e Hibernate.
- **Segurança:** Spring Security 6, senhas criptografadas em **BCrypt** e tokens **JWT (HMAC-SHA256)** com sessão Stateless.
- **Inteligência Artificial:** **Groq Cloud API** com modelo open-source **Llama 3.3 70B Versatile** (latência ultrabaixa e **custo operacional zero**).
- **Documentação de APIs:** **Springdoc OpenAPI 3.0** e interface interativa **Swagger UI**.
- **Qualidade & Confiabilidade:** **70 testes automatizados** (Surefire / JUnit 5 / MockMvc) com 100% de sucesso.

### 📱 Front-end
- **Tecnologias:** React Native com Expo e TypeScript.

---

## 🚀 Principais Módulos do Sistema

1. **🔐 Autenticação & Perfil (`/api/auth`, `/api/users`)**:
   - Cadastro, login seguro, emissão de JWT e consulta de perfil (`/api/users/profile` e `/api/users/me`).
2. **📚 Gestão de Estudos (`/api/subjects`, `/api/topics`, `/api/study-materials`)**:
   - Organização hierárquica Matéria ➔ Assuntos ➔ Anotações e Resumos.
   - **Isolamento Multitenancy:** cada estudante tem acesso estrito apenas aos seus próprios dados.
3. **🤖 Tutor de Estudos com IA Groq (`/api/ai`)**:
   - Esclarecimento de dúvidas com contexto do assunto estudado (`/api/ai/ask`).
   - Síntese inteligente e extração de conceitos-chave (`/api/ai/summarize`).
   - Mecanismo de contingência pedagógica com respostas resilientes.
4. **📝 Simulados Dinâmicos e Avaliação (`/api/quizzes`, `/api/questions`)**:
   - Banco de questões de múltipla escolha com gabarito único.
   - Geração de simulados com **10 ou 15 questões**.
   - **Arquitetura Anti-Fraude:** ocultação rígida de respostas corretas e explicações durante a prova (`IN_PROGRESS`).
   - Correção automática imediata pós-submissão (`COMPLETED`) com cálculo de notas (escala de 0.0 a 10.0) e gabarito comentado.
5. **📊 Painel de Desempenho e Recomendações (`/api/performance`)**:
   - Resumo geral de taxa de acerto acumulada e distribuição por matéria.
   - Classificação algorítmica de maturidade dos assuntos:
     - 🔴 **`CRITICAL`** (< 50% de acerto)
     - 🟡 **`REGULAR`** (50% a 74.9%)
     - 🟢 **`MASTERED`** (≥ 75%)
   - Sugestão automática de materiais didáticos cadastrados e geração de **plano de estudos sob medida** formulado pela IA Groq.

---

## ⚡ Como Rodar o Back-end

### Pré-requisitos
- **Java 21** instalado (`java -version`).
- **MySQL 8** em execução.

### Executando o Servidor:
```bash
# Entre na pasta do backend
cd backend

# Inicie o servidor Spring Boot
./mvnw spring-boot:run
```

O servidor iniciará em **`http://localhost:8080`**.

### Executando os 70 Testes Automatizados:
```bash
cd backend
./mvnw test
```

---

## 🌐 Testando a API

### 1. Pelo Navegador (Swagger UI Interativo)
Com o back-end rodando, acesse:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**  
*(Ou a especificação bruta OpenAPI JSON em `http://localhost:8080/v3/api-docs`)*

### 2. Pelo Postman / Insomnia
Importe com 1 clique a coleção oficial incluída na raiz do projeto:
📂 [`TrilhaMais_API_Postman_Collection.json`](./TrilhaMais_API_Postman_Collection.json)

---

## 🧭 Material de Apoio e Apresentação

- 📄 [`backend/README.md`](./backend/README.md): Documentação aprofundada da arquitetura do back-end e inventário técnico de arquivos.
- 🎓 [`Guia_Apresentacao_Banca_UNINASSAU.md`](./Guia_Apresentacao_Banca_UNINASSAU.md): Roteiro de fala, passo a passo para a Live Demo no Swagger e banco de respostas para a banca examinadora.

---

*UNINASSAU Caruaru — Análise e Desenvolvimento de Sistemas | 2026*
