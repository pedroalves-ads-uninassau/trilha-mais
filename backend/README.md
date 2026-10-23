# 🚀 Trilha+ — Back-end RESTful API

> **Plataforma Inteligente de Aprendizagem Universitária**  
> **Centro Universitário Maurício de Nassau (UNINASSAU) — Caruaru/PE**  
> Curso de Análise e Desenvolvimento de Sistemas | Laboratório de Empreendimentos Inovadores  
> **Responsáveis Técnicos:** Pedro Francisco Alves Neto & Allan Victor Morais De Lima  
>  
> 🚀 **API em Produção (Live):** [https://trilha-mais-backend.onrender.com](https://trilha-mais-backend.onrender.com/)  
> 📖 **Swagger UI Oficial:** [https://trilha-mais-backend.onrender.com/swagger-ui/index.html](https://trilha-mais-backend.onrender.com/swagger-ui/index.html)  

---

## 📌 Visão Geral da Arquitetura

O back-end do **Trilha+** foi desenvolvido em **Java 21 (LTS)** e **Spring Boot 4**, seguindo os princípios de arquitetura em camadas (Controller, Service, Repository, DTO, Model, Config e Exception Handling). A API é totalmente **Stateless**, autenticada por **JSON Web Tokens (JWT)** e persistida no banco relacional **PostgreSQL**.

A plataforma integra recursos de **Inteligência Artificial Generativa gratuita (Groq Cloud / Llama 3.3 70B)** para tutoria acadêmica com chat conversacional contínuo (multi-turn), geração de resumos e planos de estudo, além de módulo de **Agenda de Estudos**, **Dashboard unificado** para a aplicação mobile e motor de **Simulados Dinâmicos** com proteção anti-fraude e diagnóstico de pontos fortes e fracos.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia | Detalhes |
| :--- | :--- | :--- |
| **Linguagem** | Java 21 (LTS) | Recursos modernos da JVM e tipagem forte |
| **Framework** | Spring Boot 4 | Injeção de dependências e ecossistema Spring MVC |
| **Persistência** | Spring Data JPA / Hibernate | Mapeamento Objeto-Relacional (ORM) e tipos `TEXT` portáveis |
| **Banco de Dados** | PostgreSQL | Dialeto oficial `PostgreSQLDialect`, constraints e integridade relacional |
| **Segurança** | Spring Security + JJWT + BCrypt | Sessão Stateless, senhas criptografadas e Bearer Tokens |
| **Inteligência Artificial** | Groq Cloud (Llama 3.3 70B) | Chat multi-turn e tutoria acadêmica de alta performance |
| **Documentação** | Springdoc OpenAPI 3.0 / Swagger UI | Interface interativa no navegador para testes de rotas |
| **Testes Automatizados**| JUnit 5 / MockMvc / Surefire | Suíte de testes automatizados com validação contínua |

---

## 🔑 Principais Módulos do Sistema

1. **Autenticação e Usuários (`/api/auth`, `/api/users`)**:
   - Cadastro com validação de formato de e-mail e força de senha.
   - Criptografia irreversível de senhas com algoritmo **BCrypt**.
   - Emissão e validação de tokens **JWT (HMAC-SHA256)** com expiração de 24h.
   - Perfil do estudante protegido (`/api/users/me`) retornando dados consolidados de ofensiva (`streakDays`), avatar emoji, percentual de acurácia (`overallAccuracy`) e total de simulados.

2. **Dashboard Unificado (`/api/dashboard`)**:
   - Fornece em uma única chamada `GET /api/dashboard` todos os dados necessários para renderizar a tela inicial do mobile:
     - Dias em sequência (`streakDays`).
     - Progresso geral do aluno (`overallProgress`) e precisão (`overallAccuracy`).
     - Card "Continuar de onde parou" (`continueTopic`).
     - Lista de matérias com tokens de cor, ícone e contadores de progresso.

3. **Agenda & Cronograma de Estudos (`/api/schedules`)**:
   - CRUD completo de compromissos de estudo.
   - Endpoint rápido `PATCH /api/schedules/{id}/toggle` para marcar/desmarcar tarefas como concluídas.
   - Ordenação cronológica automática por data e horário.

4. **Organização Didática — Matérias e Assuntos (`/api/subjects`, `/api/topics`)**:
   - Árvore hierárquica `Subject (Matéria)` ➔ `Topic (Assunto)` ➔ `StudyMaterial (Material Didático)`.
   - **Isolamento Multitenancy Rigoroso**: cada estudante visualiza e manipula exclusivamente seus próprios conteúdos acadêmicos.
   - Atributos visuais em Matérias: `colorKey`, `primaryColor` (hexadecimal) e `icon` (emoji).
   - Cálculo dinâmico em tempo de execução: contagem de tópicos concluídos (`completedCount`), em andamento (`inProgressCount`) e percentual de avanço (`progressPercentage`).
   - Endpoint rápido `PATCH /api/topics/{id}/status?status=...` para atualizar status entre `pendente`, `andamento` e `concluido`.

5. **Tutor Inteligente de IA (`/api/ai`)**:
   - Chat conversacional multi-turn (`POST /api/ai/chat`) mantendo histórico completo de diálogo com o aluno.
   - Tira-dúvidas pontual contextualizado (`POST /api/ai/ask`).
   - Síntese e extração de tópicos-chave (`POST /api/ai/summarize`).
   - **Tolerância a Falhas**: Fallback pedagógico automático em caso de indisponibilidade da API externa.

6. **Sistema de Quiz e Simulados Avaliativos (`/api/quizzes`, `/api/questions`)**:
   - Banco de questões de múltipla escolha com gabarito único validado.
   - Geração dinâmica de simulados com **10 ou 15 questões** por tópico ou matéria.
   - **Arquitetura Anti-Fraude**: Durante a realização da prova (`IN_PROGRESS`), as alternativas corretas e explicações são rigidamente omitidas do JSON.
   - Correção imediata após a entrega (`COMPLETED`), com cálculo de nota (0.0 a 10.0), gabarito comentado e listas pedagógicas de **pontos fortes** e **pontos fracos** calculadas dinamicamente.

7. **Analytics de Desempenho e Recomendações (`/api/performance`)**:
   - Resumo global com cálculo de acurácia percentual e histórico decrescente.
   - Diagnóstico por assunto:
     - 🔴 **`CRITICAL`**: Menos de 50% de acerto.
     - 🟡 **`REGULAR`**: Entre 50% e 74.9% de acerto.
     - 🟢 **`MASTERED`**: 75% ou mais de acerto.
   - Recomendação de materiais vinculados aos tópicos fracos e geração de roteiro de estudos pela IA Groq.

---

## 📖 Documentação Interativa das APIs (Swagger UI)

Com o back-end em execução, acesse pelo navegador:

👉 **Interface Web do Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
👉 **Especificação OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Como testar rotas autenticadas no Swagger:**
> 1. Execute `POST /api/auth/login` informando seu e-mail e senha cadastrados.
> 2. Copie o valor do campo `accessToken`.
> 3. Clique no botão **Authorize 🔒** no topo direito da tela do Swagger.
> 4. Cole o token no campo de valor e clique em **Authorize**. Pronto! Todas as requisições subsequentes incluirão `Authorization: Bearer <token>`.

---

## 📋 Catálogo Completo de Endpoints REST

### 1. Dashboard e Visão Geral
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/dashboard` | Sim | Consolida resumo da Home (ofensiva, progresso, matérias, continuar) |

### 2. Agenda e Cronograma de Estudos
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/schedules` | Sim | Lista compromissos de estudo ordenados cronologicamente |
| `POST` | `/api/schedules` | Sim | Cria compromisso (pode ligar a `subjectId`/`topicId` e definir `scheduledDate`) |
| `PUT` | `/api/schedules/{id}` | Sim | Edita compromisso (título, data, horário, matéria/assunto) |
| `PATCH` | `/api/schedules/{id}/toggle` | Sim | Alterna status de conclusão do compromisso |
| `DELETE` | `/api/schedules/{id}` | Sim | Exclui compromisso |

### 3. Autenticação e Usuários
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/users/register` | Não | Cadastra um novo estudante |
| `POST` | `/api/auth/login` | Não | Autentica e retorna o JWT |
| `POST` | `/api/auth/forgot-password` | Não | Envia código de 6 dígitos por e-mail para recuperar a senha |
| `POST` | `/api/auth/reset-password` | Não | Troca a senha usando o código recebido |
| `GET` | `/api/users/profile` | Sim | Retorna os dados do estudante autenticado |
| `GET` | `/api/users/me` | Sim | Perfil completo com métricas de desempenho e ofensiva |
| `PUT` | `/api/users/me` | Sim | Edita nome e avatar |
| `PUT` | `/api/users/me/password` | Sim | Troca a senha (exige a senha atual) |
| `GET` | `/api/users/me/settings` | Sim | Configurações (notificações e tema) |
| `PUT` | `/api/users/me/settings` | Sim | Atualiza notificações e tema (`light`/`dark`) |

### 4. Matérias e Disciplinas
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/subjects` | Sim | Lista matérias com tokens visuais e métricas de progresso calculadas |
| `POST` | `/api/subjects` | Sim | Cadastra nova matéria (com colorKey, primaryColor, icon) |
| `GET` | `/api/subjects/{id}` | Sim | Consulta matéria por ID com lista de tópicos vinculados |
| `PUT` | `/api/subjects/{id}` | Sim | Atualiza dados da matéria |
| `DELETE` | `/api/subjects/{id}` | Sim | Remove matéria e seus tópicos em cascata |

### 5. Assuntos / Tópicos de Estudo
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/subjects/{subjectId}/topics` | Sim | Lista tópicos de uma matéria |
| `POST` | `/api/subjects/{subjectId}/topics` | Sim | Adiciona um tópico vinculado à matéria |
| `GET` | `/api/topics/{id}` | Sim | Consulta detalhes do tópico |
| `PUT` | `/api/topics/{id}` | Sim | Atualiza nome e descrição do tópico |
| `PATCH` | `/api/topics/{id}/status?status=...` | Sim | Atualiza status (`pendente`, `andamento`, `concluido`) |
| `DELETE` | `/api/topics/{id}` | Sim | Remove o tópico |

### 6. Materiais de Estudo Didáticos
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/topics/{topicId}/materials` | Sim | Lista materiais didáticos do tópico |
| `POST` | `/api/topics/{topicId}/materials` | Sim | Adiciona material (SUMMARY, NOTES, TEXT, ARTICLE) |
| `GET` | `/api/materials/{id}` | Sim | Consulta material por ID |
| `PUT` | `/api/materials/{id}` | Sim | Atualiza conteúdo do material |
| `DELETE` | `/api/materials/{id}` | Sim | Exclui o material |

### 7. Banco de Questões
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/topics/{topicId}/questions` | Sim | Cadastra questão com alternativas de múltipla escolha |
| `GET` | `/api/topics/{topicId}/questions` | Sim | Lista todas as questões cadastradas para o tópico |
| `GET` | `/api/questions/{id}` | Sim | Consulta detalhes da questão com alternativas e gabarito |
| `DELETE` | `/api/questions/{id}` | Sim | Remove a questão e suas alternativas |

### 8. Sistema de Quiz e Simulados
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/quizzes/generate` | Sim | Gera simulado dinâmico com gabarito oculto |
| `GET` | `/api/quizzes/{id}` | Sim | Busca dados da prova para resolução no app |
| `POST` | `/api/quizzes/{id}/submit` | Sim | Submete respostas e retorna nota, gabarito, fortes e fracos |
| `GET` | `/api/quizzes/{id}/result` | Sim | Consulta o resultado e diagnóstico da prova concluída |
| `GET` | `/api/quizzes` | Sim | Histórico de todos os simulados do estudante |

### 9. Tutor Inteligente de IA (Groq Cloud)
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/ai/chat` | Sim | **Chat conversacional multi-turn** com retenção de histórico |
| `POST` | `/api/ai/ask` | Sim | Tira dúvidas pontuais com a IA |
| `POST` | `/api/ai/summarize` | Sim | Sintetiza textos e gera tópicos de revisão |
| `GET` | `/api/ai/chat/history?subjectId=` | Sim | Histórico salvo do chat (opcionalmente por matéria) |
| `DELETE` | `/api/ai/chat/history?subjectId=` | Sim | Apaga o histórico do chat |

### 10. Resultados e Desempenho
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/performance/summary` | Sim | Resumo acumulado de acertos, erros e média de notas |
| `GET` | `/api/performance/topics` | Sim | Diagnóstico por assunto com nível de domínio (CRITICAL/MASTERED) |
| `GET` | `/api/performance/overview` | Sim | Tela Progresso: média geral, por matéria, melhor matéria e "precisa revisar" |
| `GET` | `/api/performance/recommendations` | Sim | Recomendações didáticas e plano de estudos da IA |

---

## ⚙️ Execução Local

### Pré-requisitos
- **Java 21 (LTS)** instalado (`java -version`).
- **PostgreSQL** instalado e ativo (porta `5432`).
- Banco de dados criado: `CREATE DATABASE trilha_mais;`.

### Como Executar a Aplicação Localmente
No diretório `backend/`:

```bash
./mvnw spring-boot:run
```

### Como Executar os Testes Automatizados
```bash
./mvnw test
```

### Como Gerar o Pacote JAR Executável
```bash
./mvnw package -DskipTests
```

---

*UNINASSAU Caruaru — Laboratório de Empreendimentos Inovadores | 2026*
