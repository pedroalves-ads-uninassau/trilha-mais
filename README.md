# 🎓 TRILHA+ — Plataforma Inteligente de Aprendizagem

> **Projeto Acadêmico — Análise e Desenvolvimento de Sistemas**  
> **Centro Universitário Maurício de Nassau (UNINASSAU) — Caruaru/PE**  
> **Disciplina:** Laboratório de Empreendimentos Inovadores | 4º Período  
> **Orientador:** Professor Antonio

O **TRILHA+** é uma solução educacional móvel desenvolvida como projeto acadêmico interdisciplinar para auxiliar estudantes universitários no planejamento, organização e acompanhamento contínuo da sua rotina de estudos. A plataforma centraliza em uma única experiência intuitiva: **gestão de matérias e assuntos com indicadores visuais, cronograma de estudos com agenda integrada, dashboard consolidado de desempenho, tutoria inteligente com IA Generativa multi-turn (Groq Cloud / Llama 3.3 70B), simulados avaliativos dinâmicos com proteção anti-fraude e diagnóstico detalhado de pontos fortes e fracos**.

O projeto aplica na prática conceitos avançados de **Engenharia de Software, Arquitetura de Software em Camadas, Desenvolvimento Mobile com React Native e Expo, Segurança e Autenticação JWT Stateless, Inteligência Artificial Aplicada, Modelagem de Bancos de Dados Relacionais em PostgreSQL e Testes Automatizados**.

---

## 👥 Equipe do Projeto

| Integrante | Matrícula | Responsabilidade no Projeto |
| :--- | :---: | :--- |
| **Pedro Francisco Alves Neto** | **01864946** | **Arquitetura, Desenvolvimento e Testes do Back-end** |
| **Allan Victor Morais De Lima** | **01813117** | **Desenvolvimento do Back-end e Integrações** |
| **Gabriel Henrique da Silva** | **01777141** | **Desenvolvimento Front-end / Mobile (React Native)** |
| **Vinicius Santos Cansanção** | **01813852** | **Desenvolvimento Front-end / Mobile (React Native)** |
| **Luiz Henrique Manoel da Silva** | **01777463** | **Documentação Técnica e Gestão do Projeto** |

---

## 🎯 Objetivo e Proposta de Valor

O objetivo central do **TRILHA+** é combater a procrastinação, a desorganização e a sobrecarga de conteúdos vivenciadas por estudantes universitários e concurseiros, estruturando os estudos em torno da **Jornada Contínua de Aprendizagem**:

```text
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Onde estou  │ ──> │ O que estudar│ ──> │ Como estudar │ ──> │ Como estou   │ ──> │ O que devo   │
│ (Dashboard)  │     │  (Matérias)  │     │(Resumos/IA)  │     │  evoluindo   │     │   melhorar   │
│  & Agenda    │     │  & Assuntos  │     │ & Chat Tutor │     │ (Simulados)  │     │ (Diagnóstico)│
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
```

A plataforma personaliza a trajetória do estudante, identificando assuntos críticos através de simulados e recomendando automaticamente revisões direcionadas.

---

## 🏗️ Arquitetura do Sistema

A aplicação é dividida em dois ecossistemas independentes e desacoplados, comunicando-se por meio de uma API RESTful padronizada em JSON:

```text
┌────────────────────────────────────────────────────────┐
│               FRONT-END MOBILE (Cliente)               │
│          React Native + Expo + TypeScript              │
│       React Navigation (Native Stack) + Telas          │
└───────────────────────────┬────────────────────────────┘
                            │
                            │ Requisições HTTP REST / JSON
                            │ Cabeçalho Bearer JWT (CORS Habilitado)
                            ▼
┌────────────────────────────────────────────────────────┐
│                BACK-END API (Servidor)                 │
│              Java 21 LTS + Spring Boot 4               │
│  Controladores ➔ Serviços ➔ Repositórios ➔ Entidades   │
│         Spring Security + JWT + BCrypt + Swagger       │
└───────────────┬────────────────────────┬───────────────┘
                │                        │
                ▼                        ▼
┌──────────────────────────────┐   ┌──────────────────────────────┐
│       Banco de Dados         │   │   Inteligência Artificial    │
│        PostgreSQL            │   │       Groq Cloud API         │
│  JPA / Hibernate Relacional  │   │   Llama 3.3 70B Versatile    │
└──────────────────────────────┘   └──────────────────────────────┘
```

---

## 📱 Front-end Mobile (React Native & Expo)

O aplicativo móvel foi desenvolvido em React Native com TypeScript e Expo, proporcionando interface moderna, tipagem estrita e navegação nativa e intuitiva.

### Tecnologias Utilizadas:
- **Framework:** React Native com **Expo** (`~57.0.22`)
- **Linguagem:** TypeScript (compilação estrita com `npx tsc --noEmit` sem erros)
- **Navegação:** `@react-navigation/native` e `@react-navigation/native-stack`
- **Componentes & Telas:** `react-native-safe-area-context` e `react-native-screens`
- **Estilização:** StyleSheet nativo e temas centralizados (`tokens.css`, `screenTheme.ts`)
- **Variáveis de Ambiente:** Suporte a `.env` com template `frontend/.env.example`

### Módulos e Telas Integradas:
1. **Autenticação:** Telas de `Login`, `Cadastro` e `RecuperacaoSenha`.
2. **Dashboard / Home (`Home.tsx`):**
   - Ofensiva de estudos em dias (`streakDays`), card "Continuar de onde parou".
   - Carrossel de matérias com cores, ícones e progresso calculado em tempo real.
   - Barra de navegação inferior permanente para alternância ágil de telas.
3. **Cronograma e Estudos (`Agenda.tsx`):**
   - Listagem de compromissos categorizados por dia e horário.
   - Suporte a marcação rápida de tarefas concluídas (toggle) e agendamento de novas sessões.
4. **Matérias & Assuntos (`Materias.tsx`, `Materia.tsx`, `Assuntos.tsx`):**
   - Visualização por matéria com tokens visuais (`colorKey`, `primaryColor`, `icon`).
   - Lista de assuntos com badges de status (`pendente`, `andamento`, `concluido`) e atualização com um toque.
   - Botão direto para iniciar sessão de tutoria com IA sobre a matéria/assunto atual.
5. **Tutor Inteligente de IA (`ChatIAScreen.tsx`):**
   - Chat interativo com suporte a conversas continuadas (multi-turn).
   - Contexto acadêmico pré-carregado a partir da matéria selecionada.
6. **Simulados e Avaliação (`TelaQuiz.tsx` e `TelaResultado.tsx`):**
   - Resolução de prova sem exposição prévia de gabaritos.
   - Tela de resultado com nota, percentual de aproveitamento e diagnóstico automático de **pontos fortes** e **pontos fracos a revisar**.
7. **Perfil do Aluno (`Perfil.tsx`):**
   - Estatísticas de precisão geral (`overallAccuracy`), total de questões respondidas e dias em sequência.

---

## ⚙️ Back-end RESTful API (Java 21 & Spring Boot 4)

O back-end foi construído com arquitetura robusta, segura e orientada a serviços corporativos.

### Tecnologias e Bibliotecas:
- **Linguagem:** Java 21 LTS
- **Framework:** Spring Boot 4 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`)
- **Segurança:** Spring Security com criptografia **BCrypt** e tokens **JWT (io.jsonwebtoken / HMAC-SHA256)** com expiração de 24h
- **Banco de Dados:** **PostgreSQL** com Hibernate ORM (`PostgreSQLDialect` e mapeamento `TEXT`)
- **Inteligência Artificial:** **Groq Cloud API** com modelo **Llama 3.3 70B Versatile**
- **Documentação Interativa:** Springdoc OpenAPI 3.0 e Swagger UI
- **Comunicação Cross-Origin:** Configuração global de **CORS** para integração fluida com emuladores e dispositivos mobile
- **Qualidade de Software:** Testes automatizados unitários e de integração com JUnit 5 e MockMvc

### Principais Módulos do Back-end:
1. **🔐 Autenticação & Gestão de Usuários (`/api/auth`, `/api/users`)**:
   - Registro de estudantes com validações e proteção contra e-mails duplicados.
   - Login seguro com geração de token JWT Bearer.
   - Perfil do estudante (`/api/users/me`) com dados consolidados (`streakDays`, `avatarEmoji`, `overallAccuracy`, `totalQuestionsAnswered`, `totalQuizzesCompleted`).
2. **🏠 Dashboard Unificado (`/api/dashboard`)**:
   - Endpoint consolidado (`GET /api/dashboard`) que entrega em uma única requisição todos os indicadores da Home (ofensiva, progresso global, próximo tópico a estudar e resumo de matérias com cores e progresso).
3. **📅 Agenda & Cronograma (`/api/schedules`)**:
   - Gestão completa de tarefas de estudo (`GET`, `POST`, `DELETE`).
   - Endpoint rápido `PATCH /api/schedules/{id}/toggle` para marcar compromissos como concluídos.
4. **📚 Gestão Acadêmica Multitenancy (`/api/subjects`, `/api/topics`, `/api/materials`)**:
   - Hierarquia: Matérias ➔ Assuntos/Tópicos ➔ Materiais didáticos.
   - Atributos visuais em Matérias (`colorKey`, `primaryColor`, `icon`) e contagem dinâmica de progresso (`completedCount`, `inProgressCount`, `totalTopics`, `progressPercentage`).
   - Endpoint rápido `PATCH /api/topics/{id}/status?status=concluido` para atualização instantânea pelo mobile.
5. **🤖 Tutor Educacional com IA Generativa (`/api/ai`)**:
   - Chat multi-turn conversacional (`POST /api/ai/chat`) com retenção de histórico (`messages: [{role, content}]`).
   - Tira-dúvidas pontual (`POST /api/ai/ask`) e resumos automáticos (`POST /api/ai/summarize`).
   - Mecanismo de fallback pedagógico resiliente contra indisponibilidades da rede externa.
6. **📝 Simulados Dinâmicos Anti-Fraude & Diagnóstico (`/api/quizzes`, `/api/questions`)**:
   - Banco de questões de múltipla escolha com gabarito único.
   - Geração dinâmica de simulados de **10 ou 15 questões** por matéria ou assunto.
   - **Mecanismo Anti-Fraude:** ocultação absoluta de gabaritos durante a prova (`IN_PROGRESS`).
   - Correção instantânea pós-envio (`COMPLETED`) com nota (0.0 a 10.0), gabarito comentado e listas de **pontos fortes** e **pontos fracos** geradas dinamicamente.
7. **📊 Diagnóstico de Desempenho e Recomendações (`/api/performance`)**:
   - Taxas de acerto gerais e por disciplina (`CRITICAL`, `REGULAR`, `MASTERED`).
   - Recomendações didáticas da IA com roteiro personalizado de estudos.

---

## 📋 Catálogo Completo de Endpoints REST

| Módulo | Método | Endpoint | Protegido? | Descrição |
| :--- | :---: | :--- | :---: | :--- |
| **Dashboard** | `GET` | `/api/dashboard` | Sim | Resumo consolidado para a tela inicial (ofensiva, matérias, progresso) |
| **Agenda** | `GET` | `/api/schedules` | Sim | Lista compromissos de estudo ordenados cronologicamente |
| | `POST` | `/api/schedules` | Sim | Cria novo compromisso de estudo |
| | `PATCH`| `/api/schedules/{id}/toggle` | Sim | Alterna status de conclusão do compromisso |
| | `DELETE`| `/api/schedules/{id}` | Sim | Remove compromisso de estudo |
| **Autenticação** | `POST` | `/api/users/register` | Não | Cadastra um novo estudante |
| | `POST` | `/api/auth/login` | Não | Autentica e gera token JWT Bearer |
| | `GET` | `/api/users/profile` | Sim | Consulta perfil autenticado |
| | `GET` | `/api/users/me` | Sim | Perfil completo com métricas de desempenho |
| **Matérias** | `GET` | `/api/subjects` | Sim | Lista matérias com cores, ícones e contadores de progresso |
| | `POST` | `/api/subjects` | Sim | Cadastra nova matéria (suporta colorKey, primaryColor, icon) |
| | `GET` | `/api/subjects/{id}` | Sim | Detalhes da matéria e lista de seus tópicos |
| | `PUT` | `/api/subjects/{id}` | Sim | Atualiza dados da matéria |
| | `DELETE`| `/api/subjects/{id}` | Sim | Exclui matéria e seus assuntos |
| **Assuntos** | `GET` | `/api/subjects/{subjectId}/topics` | Sim | Lista tópicos vinculados à matéria |
| | `POST` | `/api/subjects/{subjectId}/topics` | Sim | Cria tópico na matéria com status inicial |
| | `GET` | `/api/topics/{id}` | Sim | Detalhes do tópico |
| | `PUT` | `/api/topics/{id}` | Sim | Atualiza título/descrição do tópico |
| | `PATCH`| `/api/topics/{id}/status?status=...` | Sim | Atualiza status (`pendente`, `andamento`, `concluido`) |
| | `DELETE`| `/api/topics/{id}` | Sim | Remove o tópico |
| **Materiais** | `GET` | `/api/topics/{topicId}/materials` | Sim | Lista materiais didáticos do tópico |
| | `POST` | `/api/topics/{topicId}/materials` | Sim | Adiciona material didático de apoio |
| | `GET` | `/api/study-materials/{id}` | Sim | Consulta material por ID |
| | `PUT` | `/api/study-materials/{id}` | Sim | Atualiza material didático |
| | `DELETE`| `/api/study-materials/{id}` | Sim | Remove material |
| **Questões** | `POST` | `/api/topics/{topicId}/questions` | Sim | Cadastra questão com alternativas |
| | `GET` | `/api/topics/{topicId}/questions` | Sim | Lista questões do tópico |
| | `GET` | `/api/questions/{id}` | Sim | Detalhes da questão com gabarito |
| | `DELETE`| `/api/questions/{id}` | Sim | Remove questão |
| **Simulados** | `POST` | `/api/quizzes/generate` | Sim | Gera simulado com gabarito oculto |
| | `GET` | `/api/quizzes/{id}` | Sim | Caderno de prova para resolução |
| | `POST` | `/api/quizzes/{id}/submit` | Sim | Envia respostas, fecha prova e retorna nota, gabarito, fortes e fracos |
| | `GET` | `/api/quizzes/{id}/result` | Sim | Resultado detalhado com diagnóstico |
| | `GET` | `/api/quizzes` | Sim | Histórico de simulados do estudante |
| **Tutor IA** | `POST` | `/api/ai/chat` | Sim | **Chat conversacional multi-turn** com retenção de histórico |
| | `POST` | `/api/ai/ask` | Sim | Tira dúvidas pontuais sobre o tópico |
| | `POST` | `/api/ai/summarize` | Sim | Gera resumo didático e tópicos-chave |
| **Performance**| `GET` | `/api/performance/summary` | Sim | Resumo consolidado de acertos e notas |
| | `GET` | `/api/performance/topics` | Sim | Diagnóstico de domínio por assunto |
| | `GET` | `/api/performance/history` | Sim | Histórico de avaliações |
| | `GET` | `/api/performance/recommendations` | Sim | Recomendações e plano de estudo gerado pela IA |

---

## 📂 Estrutura de Pastas do Repositório

```text
trilha-mais/
├── backend/                              # Servidor API REST (Java 21 / Spring Boot 4 / PostgreSQL)
│   ├── src/main/java/com/trilhamais/backend/
│   │   ├── config/                       # Segurança, CORS, JWT e OpenAPI
│   │   ├── controller/                   # 11 Controladores REST (Dashboard, Schedule, Ai, etc.)
│   │   ├── dto/                          # Objetos de Transferência de Dados (Requests/Responses)
│   │   ├── entity/ / model/              # 12 Entidades JPA mapeadas (PostgreSQL)
│   │   ├── exception/                    # Tratamento Global de Exceções RFC 7807
│   │   ├── repository/                   # Interfaces Spring Data JPA
│   │   └── service/                      # Serviços de Regra de Negócio e IA Groq
│   ├── src/main/resources/
│   │   └── application.properties        # Configurações do PostgreSQL, JWT e Groq
│   ├── src/test/java/                    # Testes Automatizados (Unitários e E2E)
│   ├── pom.xml                           # Dependências Maven (Spring Boot 4, PostgreSQL, JJWT)
│   └── mvnw                              # Maven Wrapper executável
│
├── frontend/                             # Aplicativo Mobile (React Native / Expo / TypeScript)
│   ├── assets/                           # Ícones, splash screen e imagens
│   ├── src/
│   │   ├── components/                   # Componentes reutilizáveis (BarraNavegacao, etc.)
│   │   ├── Navigation/                   # Rotas unificadas do app (AppRoutes.tsx)
│   │   ├── pages/                        # Telas (Home, Agenda, Materia, Assuntos, ChatIA, Quiz, Perfil)
│   │   ├── services/                     # Serviços de integração (groq, api)
│   │   └── theme/                        # Paleta de cores, tipografia e espaçamentos
│   ├── .env.example                      # Template de variáveis de ambiente do mobile
│   ├── App.tsx                           # Ponto de entrada do aplicativo
│   ├── app.json                          # Configurações do Expo
│   ├── package.json                      # Dependências Node / Expo (com script build expo web)
│   ├── vercel.json                       # Configuração de rewrites SPA para Vercel
│   └── tsconfig.json                     # Configuração estrita do TypeScript
│
├── vercel.json                           # Configuração raiz da Vercel para monorepo
├── package.json                          # Scripts de orquestração raiz (npm run build)
├── TrilhaMais_API_Postman_Collection.json # Coleção oficial do Postman com todos os endpoints
├── .gitignore                            # Arquivos e credenciais ignorados pelo Git
└── README.md                             # Documentação principal do projeto
```

---

## ⚡ Como Executar o Projeto Localmente

### 1. Inicializando o Back-end (API REST)

#### Pré-requisitos:
- **Java 21 (LTS)** instalado (`java -version`)
- **PostgreSQL** instalado e em execução (porta padrão `5432`)
- Criar o banco de dados no PostgreSQL:
  ```sql
  CREATE DATABASE trilha_mais;
  ```

#### Passos:
```bash
# 1. Acesse o diretório do backend
cd backend

# 2. Inicie a aplicação com o Maven Wrapper
./mvnw spring-boot:run
```
O servidor inicializará automaticamente as tabelas e estará acessível em: **`http://localhost:8080`**.

#### Executando a Suíte de Testes Automatizados:
```bash
cd backend
./mvnw test
```

---

### 2. Inicializando o Front-end Mobile (React Native / Expo)

#### Pré-requisitos:
- **Node.js** (versão 18 ou superior) instalado
- Dispositivo Android/iOS com o aplicativo **Expo Go** instalado (ou emulador/navegador)

#### Passos:
```bash
# 1. Acesse o diretório do frontend
cd frontend

# 2. Instale as dependências
npm install

# 3. Configure as variáveis de ambiente (se necessário)
cp .env.example .env

# 4. Inicie o servidor de desenvolvimento do Expo
npx expo start
```

Após iniciar:
- Pressione **`a`** para abrir no emulador Android.
- Pressione **`i`** para abrir no simulador iOS.
- Pressione **`w`** para abrir no navegador Web.
- Ou escaneie o **QR Code** no terminal usando o aplicativo **Expo Go** no smartphone.

---

## 🚀 Guia de Deploy em Produção (Vercel & Render)

Para colocar a plataforma completa em produção e acessível publicamente na nuvem, adota-se uma arquitetura moderna dividida em dois serviços especializados com integração via variáveis de ambiente:

```text
┌─────────────────────────────────┐        Requisições REST (HTTPS)        ┌──────────────────────────────────┐
│      Front-end Web (Expo)       │ ─────────────────────────────────────> │     Back-end API (Spring Boot)   │
│             Vercel              │                                        │          Render Web Service      │
│  (https://trilha-mais.vercel.app)│ <───────────────────────────────────── │(https://trilha-mais.onrender.com)│
└─────────────────────────────────┘        Respostas JSON / CORS Liberado   └────────────────┬─────────────────┘
                                                                                             │
                                                                                             │ Conexão JDBC / Pool
                                                                                             ▼
                                                                           ┌──────────────────────────────────┐
                                                                           │        Render PostgreSQL         │
                                                                           │      (Managed Relational DB)     │
                                                                           └──────────────────────────────────┘
```

---

### 🗄️ Passo 1: Criar o Banco de Dados PostgreSQL no Render

1. Acesse sua conta no **[Render.com](https://render.com/)**.
2. No painel principal, clique no botão **"New +"** no canto superior direito e selecione **"PostgreSQL"**.
3. Preencha as configurações do banco:
   - **Name:** `trilha-mais-db`
   - **Database:** `trilha_mais`
   - **User:** `trilha_user` (ou mantenha o gerado automaticamente)
   - **Region:** Escolha a região mais próxima (ex: `Ohio (US East)` ou `Frankfurt (EU Central)`). *Dica: anote a região para usar a mesma no Back-end!*
   - **PostgreSQL Version:** `16` (ou padrão)
   - **Instance Type:** `Free`
4. Clique em **"Create Database"**.
5. Aguarde o banco ficar com o status **`Available`**.
6. Na aba **"Info"** do banco criado, localize a seção **"Connections"**:
   - Copie o **Internal Database URL** (recomendado se o Web Service estiver na mesma região) ou os campos individuais:
     - **Hostname:** `dpg-xxxx.render.com`
     - **Port:** `5432`
     - **Database:** `trilha_mais`
     - **Username:** seu usuário
     - **Password:** sua senha

---

### ☕ Passo 2: Hospedar o Back-end Spring Boot no Render

1. No painel do **Render**, clique em **"New +"** ➔ **"Web Service"**.
2. Conecte sua conta do GitHub e selecione o repositório **`pedroalves-ads-uninassau/trilha-mais`**.
3. Defina os parâmetros de deploy:
   - **Name:** `trilha-mais-backend` (ou o nome desejado)
   - **Region:** A mesma região selecionada para o banco PostgreSQL (ex: `Ohio`)
   - **Branch:** `main`
   - **Root Directory:** `backend` *(Obrigatório! O projeto Java reside nessa subpasta)*
   - **Runtime:** `Java`
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/backend-0.0.1-SNAPSHOT.jar`
   - **Instance Type:** `Free`
4. Na seção **"Environment Variables"**, adicione as seguintes variáveis:

| Chave (Key) | Valor (Value) | Descrição |
| :--- | :--- | :--- |
| `PORT` | `8080` | Porta interna da aplicação (o Render mapeia para 443 HTTPS) |
| `DB_HOST` | Hostname do banco copiado no Passo 1 | Host do PostgreSQL Render |
| `DB_PORT` | `5432` | Porta padrão do PostgreSQL |
| `DB_NAME` | `trilha_mais` | Nome do banco criado |
| `DB_USERNAME` | Usuário do banco copiado no Passo 1 | Usuário de autenticação do DB |
| `DB_PASSWORD` | Senha do banco copiada no Passo 1 | Senha de autenticação do DB |
| `JWT_SECRET` | `9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b` | Chave criptográfica HMAC-SHA256 para tokens JWT |
| `JWT_EXPIRATION_MS` | `86400000` | Duração do token JWT (24 horas) |
| `GROQ_API_KEY` | Sua chave de API da Groq Cloud (`gsk_...`) | Acesso ao Tutor de IA Llama 3.3 70B |
| `GROQ_MODEL` | `llama-3.3-70b-versatile` | Modelo LLM utilizado |

> **Alternativa com URL direta:** Se preferir, você pode definir apenas a variável `SPRING_DATASOURCE_URL` com a **Internal Database URL** do PostgreSQL (lembrando de usar o prefixo `jdbc:postgresql://...`).

5. Clique em **"Create Web Service"**.
6. O Render iniciará a compilação do Maven e a inicialização do Spring Boot.
7. Quando o log exibir `Started BackendApplication in X seconds` e o status mudar para **`Live`**, copie a URL gerada (ex: **`https://trilha-mais-backend.onrender.com`**).
8. **Validação:** Acesse no navegador `https://trilha-mais-backend.onrender.com/swagger-ui.html` para certificar-se de que a API e o Swagger estão online e com as tabelas criadas automaticamente pelo Hibernate!

---

### 🌐 Passo 3: Hospedar o Front-end Expo Web na Vercel

O repositório já conta com o arquivo [`vercel.json`](./vercel.json) e scripts de build configurados na raiz e na pasta `frontend/`, permitindo que a Vercel compile a aplicação React Native Web com o comando `expo export --platform web`.

1. Acesse o painel do seu projeto na **[Vercel](https://vercel.com/)** (`trilha-mais`).
2. Acesse a aba **Settings** ➔ **Environment Variables**:
   Adicione as seguintes variáveis de ambiente:

| Chave (Key) | Valor (Value) | Observação |
| :--- | :--- | :--- |
| `EXPO_PUBLIC_API_URL` | `https://trilha-mais-backend.onrender.com` | **URL do seu backend no Render** (sem barra `/` no final) |
| `EXPO_PUBLIC_GROQ_API_KEY` | `gsk_...` | Chave de API da Groq Cloud para IA no cliente |
| `EXPO_PUBLIC_GROQ_MODEL` | `llama-3.3-70b-versatile` | Modelo LLM configurado |

3. Acesse a aba **Deployments**, clique nos três pontinhos (`...`) ao lado do deploy atual e selecione **"Redeploy"** (ou envie um novo commit para a branch `main`).
4. A Vercel executará o script de exportação estática do Expo e publicará a aplicação.
5. Pronto! Acesse **`https://trilha-mais.vercel.app`** para utilizar a aplicação com interface web responsiva, navegação fluida, conexão direta ao backend Spring Boot e banco PostgreSQL!

---

## 🌐 Testes e Documentação das APIs REST

### 1. Swagger UI Interativo (Via Navegador)
Com o back-end em execução, acesse:  
👉 **`http://localhost:8080/swagger-ui.html`**  
*(Ou a especificação bruta OpenAPI JSON em `http://localhost:8080/v3/api-docs`)*

- Para testar rotas protegidas, clique no botão **`Authorize 🔒`** no canto superior direito e insira o token obtido no login no formato: `Bearer seu_token_jwt`.

---

### 2. Coleção do Postman (Guia Oficial)

O repositório inclui a coleção oficial completa e atualizada: [`TrilhaMais_API_Postman_Collection.json`](./TrilhaMais_API_Postman_Collection.json).

#### 📥 Como Importar no Postman:
1. Abra o **Postman** (Desktop ou Web).
2. Clique no botão **"Import"** no canto superior esquerdo (ou `Ctrl + O` / `Cmd + O`).
3. Selecione ou arraste o arquivo **`TrilhaMais_API_Postman_Collection.json`** da raiz do projeto.
4. A coleção **"Trilha+ API — Coleção Postman Oficial (UNINASSAU)"** será carregada com todas as pastas organizadas.

#### ⚙️ Gerenciamento Automático de Tokens:
- A requisição **"Login (Obter Token JWT)"** possui script pós-execução que armazena automaticamente o token retornado na variável `{{token}}`.
- Todas as demais requisições autenticadas já herdam automaticamente o cabeçalho `Authorization: Bearer {{token}}`. Não é necessário copiar e colar tokens manualmente!

---

## ⚠️ Nota Acadêmica

Este projeto é desenvolvido com finalidade estritamente educacional para a conclusão de disciplinas práticas no curso de **Análise e Desenvolvimento de Sistemas** da **UNINASSAU — Caruaru**.

---

*UNINASSAU Caruaru — Análise e Desenvolvimento de Sistemas | 2026*
