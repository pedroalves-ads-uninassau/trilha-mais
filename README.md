# 🎓 TRILHA+ — Plataforma Inteligente de Aprendizagem

> **Projeto Acadêmico — Análise e Desenvolvimento de Sistemas**  
> **Centro Universitário Maurício de Nassau (UNINASSAU) — Caruaru/PE**  
> **Disciplina:** Laboratório de Empreendimentos Inovadores | 4º Período  
> **Orientador:** Professor Antonio

O **TRILHA+** é uma solução educacional móvel e web desenvolvida como projeto acadêmico interdisciplinar para auxiliar estudantes universitários no planejamento, organização e acompanhamento contínuo da sua rotina de estudos. A plataforma centraliza em uma única experiência intuitiva: **gestão de matérias e tópicos, materiais didáticos, tutoria inteligente com IA Generativa (Groq Cloud / Llama 3.3 70B), simulados avaliativos dinâmicos com proteção anti-fraude e diagnóstico analítico de desempenho acadêmico**.

O projeto aplica na prática conceitos avançados de **Engenharia de Software, Arquitetura de Software em Camadas, Desenvolvimento Mobile com React Native, Segurança e Autenticação JWT, Inteligência Artificial Aplicada, Modelagem de Bancos de Dados Relacionais e Testes Automatizados**.

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

O objetivo central do **TRILHA+** é combater a desorganização e a sobrecarga de conteúdos vivenciadas por estudantes universitários e concurseiros, estruturando os estudos em torno da **Jornada Contínua de Aprendizagem**:

```text
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Onde estou  │ ──> │ O que estudar│ ──> │ Como estudar │ ──> │ Como estou   │ ──> │ O que devo   │
│ (Diagnóstico)│     │  (Matérias)  │     │ (Resumos/IA) │     │  evoluindo   │     │   melhorar   │
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
│        MySQL 8.0             │   │       Groq Cloud API         │
│  JPA / Hibernate Relacional  │   │  LLaMA 3.3 70B Versatile     │
└──────────────────────────────┘   └──────────────────────────────┘
```

---

## 📱 Front-end Mobile (React Native & Expo)

O front-end móvel foi estruturado para proporcionar uma experiência fluida, moderna e responsiva em dispositivos Android e iOS.

### Tecnologias Utilizadas:
- **Framework:** React Native com **Expo** (`~57.0.22`)
- **Linguagem:** TypeScript
- **Roteamento & Telas:** `@react-navigation/native` e `@react-navigation/native-stack`
- **Componentes de Interface:** `react-native-safe-area-context` e `react-native-screens`
- **Ambiente de Execução:** Suporte a emuladores Android/iOS, navegador Web e dispositivos físicos via Expo Go

### Telas e Módulos do Aplicativo:
- **Tela de Login / Autenticação:** Acesso seguro com validação de credenciais.
- **Tela Home / Dashboard:** Visão geral das matérias em andamento e progresso.
- **Tela de Matérias:** Listagem de disciplinas com indicadores de tópicos.
- **Tela de Assuntos:** Detalhamento dos tópicos dentro de cada matéria.
- **Tela de Avaliação / Simulado:** Interface de resolução de questões com navegação sequencial.
- **Tela de Resultado:** Exibição da nota obtida, percentual de acerto e gabarito comentado.
- **Tela de Perfil do Estudante:** Dados cadastrais e histórico de conquistas.
- **Tela de Chat / Tutor IA:** Diálogo interativo para tirar dúvidas com o assistente pedagógico.

---

## ⚙️ Back-end RESTful API (Java 21 & Spring Boot 4)

O back-end foi construído com foco em alta confiabilidade, segurança e aderência aos padrões de mercado da engenharia de software corporativa.

### Tecnologias e Bibliotecas:
- **Linguagem:** Java 21 LTS
- **Framework:** Spring Boot 4 (Spring Data JPA, Spring Web MVC, Spring Validation)
- **Segurança:** Spring Security com criptografia **BCrypt** e tokens **JWT (io.jsonwebtoken / HMAC-SHA256)**
- **Banco de Dados:** MySQL 8.0 com Hibernate ORM
- **Inteligência Artificial:** **Groq Cloud API** com modelo **Llama 3.3 70B Versatile**
- **Documentação Interativa:** Springdoc OpenAPI 3.0 e interface Swagger UI
- **Comunicação Cross-Origin:** Suporte completo a **CORS** para integração mobile sem restrições
- **Qualidade de Software:** **70 testes automatizados** aprovados (100% de sucesso)

### Principais Módulos do Back-end:
1. **🔐 Autenticação & Gestão de Usuários (`/api/auth`, `/api/users`)**:
   - Registro de estudantes com validações de dados e verificação de e-mail duplicado.
   - Login seguro com geração de token JWT Bearer.
   - Consulta de perfil autenticado (`/api/users/profile` e `/api/users/me`).
2. **📚 Gestão Acadêmica Multitenancy (`/api/subjects`, `/api/topics`, `/api/materials`)**:
   - Cadastro e gerenciamento hierárquico: Matérias ➔ Tópicos ➔ Materiais didáticos.
   - Isolamento rígido de dados por estudante autenticado.
3. **🤖 Tutor Educacional Inteligente (`/api/ai`)**:
   - Respostas contextualizadas ao conteúdo em estudo com modelo Llama 3.3 70B via Groq.
   - Resumos automáticos e extração didática de conceitos-chave.
   - Mecanismo de fallback pedagógico resiliente contra indisponibilidades.
4. **📝 Simulados Dinâmicos Anti-Fraude (`/api/quizzes`, `/api/questions`)**:
   - Banco de questões de múltipla escolha com gabarito único validado.
   - Geração dinâmica de simulados de **10 ou 15 questões** por matéria ou tópico.
   - **Mecanismo Anti-Fraude:** ocultação absoluta do gabarito durante a realização da prova (`IN_PROGRESS`).
   - Correção imediata pós-envio (`COMPLETED`) com cálculo de nota (0.0 a 10.0), percentual e gabarito comentado.
5. **📊 Diagnóstico de Desempenho e Recomendações (`/api/performance`)**:
   - Taxas de acerto gerais e por disciplina.
   - Classificação algorítmica de maturidade:
     - 🔴 **`CRITICAL`** (< 50% de acerto)
     - 🟡 **`REGULAR`** (50% a 74.9%)
     - 🟢 **`MASTERED`** (≥ 75%)
   - Recomendação de materiais internos e elaboração de plano de estudos personalizado pela IA.

---

## 📂 Estrutura de Pastas do Repositório

O projeto é organizado com separação estrita de responsabilidades:

```text
trilha-mais/
├── backend/                              # Servidor da API REST (Java 21 / Spring Boot 4)
│   ├── src/main/java/com/trilhamais/backend/
│   │   ├── config/                       # Segurança, CORS, JWT e OpenAPI
│   │   ├── controller/                   # 9 Controladores REST
│   │   ├── dto/                          # 32 Objetos de Transferência de Dados
│   │   ├── entity/ / model/              # 11 Entidades JPA e Enums
│   │   ├── exception/                    # Tratamento Global de Exceções RFC 7807
│   │   ├── repository/                   # 9 Interfaces Spring Data JPA
│   │   └── service/                      # 9 Serviços com Regras de Negócio e IA
│   ├── src/test/java/                    # 70 Testes Automatizados (Unitários e E2E)
│   ├── pom.xml                           # Dependências e Plugins do Maven
│   └── mvnw                              # Maven Wrapper executável
│
├── frontend/                             # Aplicativo Mobile (React Native / Expo)
│   ├── assets/                           # Ícones, splash screens e imagens
│   ├── src/
│   │   ├── Navigation/                   # Configuração de rotas de navegação
│   │   ├── pages/                        # Telas (Login, Home, Matérias, Quiz, Chat)
│   │   ├── Mocks/                        # Dados simulados para prototipação
│   │   └── types/                        # Tipagens TypeScript da aplicação
│   ├── App.tsx                           # Componente raiz do aplicativo
│   ├── app.json                          # Configurações do Expo
│   ├── package.json                      # Dependências do ecossistema Node/Expo
│   └── tsconfig.json                     # Configuração do TypeScript
│
├── TrilhaMais_API_Postman_Collection.json # Coleção Postman pronta para testes
├── .gitignore                            # Arquivos e diretórios ignorados
└── README.md                             # Documentação principal do projeto
```

---

## ⚡ Como Executar o Projeto Localmente

### 1. Inicializando o Back-end (API REST)

#### Pré-requisitos:
- **Java 21 (LTS)** instalado (`java -version`)
- **MySQL 8.0** ativo com o banco configurado

#### Passos:
```bash
# 1. Acesse o diretório do backend
cd backend

# 2. Inicie a aplicação com o Maven Wrapper
./mvnw spring-boot:run
```
O servidor estará disponível em: **`http://localhost:8080`**.

#### Executando a Suíte de 70 Testes Automatizados:
```bash
cd backend
./mvnw test
```

---

### 2. Inicializando o Front-end Mobile (React Native / Expo)

#### Pré-requisitos:
- **Node.js** (versão 18 ou superior) instalado
- Aplicativo **Expo Go** instalado no smartphone (opcional para testes físicos)

#### Passos:
```bash
# 1. Acesse o diretório do frontend
cd frontend

# 2. Instale as dependências
npm install

# 3. Inicie o servidor de desenvolvimento do Expo
npx expo start
```
Após a inicialização:
- Pressione **`a`** para abrir no emulador Android conectado.
- Pressione **`i`** para abrir no simulador iOS (macOS).
- Pressione **`w`** para abrir no navegador Web.
- Ou escaneie o **QR Code** exibido no terminal utilizando o app **Expo Go** no smartphone conectado à mesma rede Wi-Fi.

---

## 🌐 Testes e Documentação das APIs REST

### 1. Swagger UI Interativo (Via Navegador)
Com o back-end em execução, acesse no navegador:  
👉 **`http://localhost:8080/swagger-ui.html`**  
*(Ou a especificação bruta OpenAPI JSON em `http://localhost:8080/v3/api-docs`)*

- Para testar endpoints protegidos, clique no botão **`Authorize 🔒`** no topo da tela do Swagger e insira o token obtido no login no formato: `Bearer seu_token_jwt`.

### 2. Coleção do Postman (Guia Passo a Passo de Importação e Uso)

O repositório inclui uma coleção oficial completa e pronta para uso: [`TrilhaMais_API_Postman_Collection.json`](./TrilhaMais_API_Postman_Collection.json).

#### 📥 Como Importar no Postman:
1. Abra o aplicativo do **Postman** (Desktop ou Web).
2. No canto superior esquerdo da barra de navegação, clique no botão **"Import"** (ou use o atalho `Ctrl + O` / `Cmd + O`).
3. Arraste e solte o arquivo **`TrilhaMais_API_Postman_Collection.json`** na janela de importação (ou clique em **"files"** e selecione o arquivo na raiz deste repositório).
4. Confirme a importação clicando no botão **"Import"**.
5. A coleção **"Trilha+ API — Coleção Postman Oficial (UNINASSAU)"** aparecerá instantaneamente na aba lateral **Collections**, estruturada em 9 pastas modulares.

#### ⚙️ Como Funciona o Gerenciamento Automático de Tokens:
- A coleção possui duas variáveis nativas pré-configuradas:
  - **`baseUrl`**: Definida por padrão como `http://localhost:8080`.
  - **`token`**: Variável dinâmica que armazena o token JWT.
- **Automação Inteligente:** A requisição **"Login (Obter Token JWT)"** possui um script de pós-execução (*Tests Script*) que intercepta o `accessToken` retornado pelo servidor e o salva automaticamente na variável `{{token}}` da coleção:
  ```javascript
  var jsonData = pm.response.json();
  if (jsonData.accessToken) {
      pm.collectionVariables.set('token', jsonData.accessToken);
      console.log('Token JWT capturado com sucesso!');
  }
  ```
- **Sem esforço manual:** Todas as requisições protegidas subsequentes já herdam e enviam automaticamente o cabeçalho `Authorization: Bearer {{token}}`. Você **não precisa** copiar e colar tokens manualmente entre as requisições!

#### 🚀 Fluxo de Execução Recomendado no Postman:
1. **Inicialize a API:** Certifique-se de que o back-end está ativo (`./mvnw spring-boot:run`).
2. **Registro de Aluno:** Na pasta `01. Autenticação & Usuários`, execute **"Cadastrar Novo Estudante"** (`POST /api/users/register`).
3. **Login Automático:** Execute **"Login (Obter Token JWT)"** (`POST /api/auth/login`). Observe o console confirmando a captura do token JWT.
4. **Verificação de Perfil:** Execute **"Consultar Perfil do Estudante (/api/users/me)"** para confirmar a autenticação Bearer.
5. **Criação de Conteúdo:**
   - Na pasta `02. Matérias`, execute **"Cadastrar Nova Matéria"** (ex: *Estrutura de Dados*).
   - Na pasta `03. Assuntos`, execute **"Cadastrar Assunto na Matéria"** (ex: *Árvores Binárias*).
   - Na pasta `04. Materiais Didáticos`, execute **"Cadastrar Material de Apoio"**.
6. **Assistência com IA Generativa:**
   - Na pasta `08. Tutor IA (Groq LLaMA 3.3)`, execute **"Tirar Dúvida Aberta com o Tutor IA"** ou **"Gerar Resumo Didático de Conteúdo"**.
7. **Simulado Anti-Fraude e Avaliação:**
   - Na pasta `05. Banco de Questões`, cadastre questões de múltipla escolha com alternativas.
   - Na pasta `06. Simulados Inteligentes`, execute **"Gerar Simulado Dinâmico (10 Questões)"**.
   - Abra o simulado em **"Consultar Caderno de Prova"** e note o sigilo de gabarito.
   - Execute **"Submeter Respostas e Corrigir Simulado"** para receber nota de 0.0 a 10.0 e gabarito comentado.
8. **Análise de Desempenho:**
   - Na pasta `07. Painel de Desempenho`, consulte **"Resumo Geral de Desempenho"** e **"Diagnóstico e Recomendações da IA"**.

---

## ⚠️ Nota Acadêmica

Este projeto é desenvolvido com finalidade estritamente educacional para a conclusão de disciplinas práticas no curso de **Análise e Desenvolvimento de Sistemas** da **UNINASSAU — Caruaru**.

---

*UNINASSAU Caruaru — Análise e Desenvolvimento de Sistemas | 2026*

