# 🚀 Trilha+ — Back-end RESTful API

> **Plataforma Inteligente de Aprendizagem Universitária**  
> **Centro Universitário Maurício de Nassau (UNINASSAU) — Caruaru/PE**  
> Curso de Análise e Desenvolvimento de Sistemas | Laboratório de Empreendimentos Inovadores  
> **Responsáveis Técnicos:** Pedro Francisco Alves Neto & Allan Victor Morais De Lima

---

## 📌 Visão Geral da Arquitetura

O back-end do **Trilha+** foi desenvolvido em **Java 21 (LTS)** e **Spring Boot 4**, seguindo os princípios de arquitetura em camadas (Controller, Service, Repository, DTO, Model, Config e Exception Handling). A API é totalmente **Stateless**, autenticada por **JSON Web Tokens (JWT)** e persistida em banco relacional **MySQL 8.0**.

A plataforma integra recursos de **Inteligência Artificial Generativa gratuita (Groq Cloud / Llama 3.3 70B)** para tutoria acadêmica, geração de resumos e planos de estudo personalizados, além de um motor completo de **Simulados Dinâmicos** com proteção anti-fraude e métricas de desempenho.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia | Detalhes |
| :--- | :--- | :--- |
| **Linguagem** | Java 21 (LTS) | Recursos modernos da JVM e tipagem forte |
| **Framework** | Spring Boot 4 | Injeção de dependências e ecossistema Spring |
| **Persistência** | Spring Data JPA / Hibernate | Mapeamento Objeto-Relacional (ORM) e Derived Queries |
| **Banco de Dados** | MySQL 8.0 | Constraints relacionais, integridade referencial e índices |
| **Segurança** | Spring Security + JJWT + BCrypt | Sessão Stateless, senhas criptografadas e Bearer Tokens |
| **Inteligência Artificial** | Groq Cloud (Llama 3.3 70B) | Respostas didáticas ultra-rápidas a custo operacional zero |
| **Documentação** | Springdoc OpenAPI 3.0 / Swagger UI | Interface interativa no navegador para testes de rotas |
| **Testes Automatizados**| JUnit 5 / MockMvc / Surefire | **70 testes automatizados** com 100% de sucesso |

---

## 🔑 Principais Módulos do Sistema

1. **Autenticação e Usuários (`/api/auth`, `/api/users`)**:
   - Cadastro com validação de formato de e-mail e força de senha.
   - Criptografia irreversível de senhas com algoritmo **BCrypt**.
   - Emissão e validação de tokens **JWT (HMAC-SHA256)** com expiração de 24h.
   - Perfil do estudante protegido por Bearer Token (`/api/users/profile` e `/api/users/me`).

2. **Organização Didática — Matérias e Assuntos (`/api/subjects`, `/api/topics`)**:
   - Árvore hierárquica `Subject (Matéria)` -> `Topic (Assunto)` -> `StudyMaterial (Material Didático)`.
   - **Isolamento Multitenancy Rigoroso**: cada estudante visualiza e manipula exclusivamente seus próprios conteúdos acadêmicos.

3. **Tutor Inteligente de IA (`/api/ai`)**:
   - Tiragem de dúvidas acadêmicas contextualizadas ao assunto do estudante (`/api/ai/ask`).
   - Síntese e extração de tópicos-chave de anotações e resumos (`/api/ai/summarize`).
   - **Tolerância a Falhas**: Fallback pedagógico automático em caso de instabilidade na conexão externa.

4. **Sistema de Quiz e Simulados Avaliativos (`/api/quizzes`, `/api/questions`)**:
   - Banco de questões de múltipla escolha com gabarito único validado.
   - Geração dinâmica de simulados com **10 ou 15 questões** por tópico ou matéria.
   - **Arquitetura Anti-Fraude**: Durante a realização da prova (`IN_PROGRESS`), as respostas corretas e explicações são rigidamente omitidas do JSON para impedir inspeção pelo cliente.
   - Correção automática imediata após a entrega (`COMPLETED`), com cálculo de notas na escala de 0.0 a 10.0 e gabarito comentado.

5. **Analytics de Desempenho e Recomendações (`/api/performance`)**:
   - Resumo global com cálculo de acurácia percentual e histórico cronológico decrescente.
   - Diagnóstico de domínio de cada assunto:
     - 🔴 **`CRITICAL`**: Menos de 50% de acerto nas questões.
     - 🟡 **`REGULAR`**: Entre 50% e 74.9% de acerto.
     - 🟢 **`MASTERED`**: 75% ou mais de acerto.
   - Recomendação de materiais vinculados aos tópicos fracos e geração de roteiro de estudos pela IA Groq.

6. **Interoperabilidade com o Front-end (React Native / Expo)**:
   - Configuração de **CORS global** habilitando origens locais, emuladores e dispositivos móveis.
   - Respostas de erro padronizadas em formato JSON (400, 401, 404, 409).

---

## 📖 Documentação Interativa das APIs (Swagger UI)

Com o back-end em execução, acesse pelo navegador:

👉 **Interface Web do Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
👉 **Especificação OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Como testar rotas autenticadas no Swagger:**
> 1. Execute `POST /api/auth/login` informando seu e-mail e senha cadastrados.
> 2. Copie o valor do campo `accessToken`.
> 3. Clique no botão **Authorize 🔒** no topo direito da tela do Swagger.
> 4. Cole o token no campo de valor e clique em **Authorize**. Pronto! Todas as requisições agora serão enviadas com o cabeçalho `Authorization: Bearer <token>`.

---

## 📋 Catálogo Completo de Endpoints REST

### 1. Autenticação e Usuários
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/users/register` | Não | Cadastra um novo estudante |
| `POST` | `/api/auth/login` | Não | Autentica e retorna o JWT |
| `GET` | `/api/users/profile` | Sim | Retorna os dados do estudante autenticado |
| `GET` | `/api/users/me` | Sim | Alias canônico para consulta de perfil |

### 2. Matérias e Disciplinas
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/subjects` | Sim | Lista todas as matérias do estudante com contagem de tópicos |
| `POST` | `/api/subjects` | Sim | Cadastra uma nova matéria |
| `GET` | `/api/subjects/{id}` | Sim | Consulta matéria por ID e lista seus tópicos vinculados |
| `PUT` | `/api/subjects/{id}` | Sim | Atualiza nome e descrição da matéria |
| `DELETE` | `/api/subjects/{id}` | Sim | Remove matéria e seus tópicos em cascata |

### 3. Assuntos / Tópicos de Estudo
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/subjects/{subjectId}/topics` | Sim | Lista tópicos de uma matéria |
| `POST` | `/api/subjects/{subjectId}/topics` | Sim | Adiciona um tópico vinculado à matéria |
| `GET` | `/api/topics/{id}` | Sim | Consulta detalhes do tópico |
| `PUT` | `/api/topics/{id}` | Sim | Atualiza nome e descrição do tópico |
| `DELETE` | `/api/topics/{id}` | Sim | Remove o tópico |

### 4. Materiais de Estudo Didáticos
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/topics/{topicId}/materials` | Sim | Lista materiais didáticos do tópico |
| `POST` | `/api/topics/{topicId}/materials` | Sim | Adiciona material (SUMMARY, NOTES, TEXT, ARTICLE) |
| `GET` | `/api/study-materials/{id}` | Sim | Consulta material por ID |
| `PUT` | `/api/study-materials/{id}` | Sim | Atualiza conteúdo do material |
| `DELETE` | `/api/study-materials/{id}` | Sim | Exclui o material |

### 5. Banco de Questões
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/topics/{topicId}/questions` | Sim | Cadastra questão com alternativas de múltipla escolha |
| `GET` | `/api/topics/{topicId}/questions` | Sim | Lista todas as questões cadastradas para o tópico |
| `GET` | `/api/questions/{id}` | Sim | Consulta detalhes da questão com alternativas e gabarito |
| `DELETE` | `/api/questions/{id}` | Sim | Remove a questão e suas alternativas |

### 6. Sistema de Quiz e Simulados
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/quizzes/generate` | Sim | Gera simulado dinâmico (10/15 questões) com gabarito oculto |
| `GET` | `/api/quizzes/{id}` | Sim | Busca dados da prova para resolução no app |
| `POST` | `/api/quizzes/{id}/submit` | Sim | Envia respostas, fecha a prova e retorna nota e gabarito |
| `GET` | `/api/quizzes/{id}/result` | Sim | Consulta o resultado e gabarito comentado de prova concluída |
| `GET` | `/api/quizzes` | Sim | Histórico de todos os simulados do estudante |

### 7. Tutor Inteligente de IA (Groq Cloud)
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `POST` | `/api/ai/ask` | Sim | Tira dúvidas com a IA com base no tópico de estudo |
| `POST` | `/api/ai/summarize` | Sim | Sintetiza textos e gera tópicos de revisão |

### 8. Resultados e Desempenho
| Método | Endpoint | Protegido? | Descrição |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/performance/summary` | Sim | Resumo acumulado de acertos, erros e média de notas |
| `GET` | `/api/performance/topics` | Sim | Diagnóstico por assunto com nível de domínio (CRITICAL/MASTERED) |
| `GET` | `/api/performance/history` | Sim | Histórico cronológico decrescente de simulados finalizados |
| `GET` | `/api/performance/recommendations` | Sim | Recomendações didáticas e plano de estudos da IA |

---

## ⚙️ Configuração do Ambiente e Execução Local

### Pré-requisitos
- **Java Development Kit (JDK) 21** instalado.
- **MySQL Server 8.0** em execução local ou remoto.

### Variáveis de Ambiente Suportadas
As configurações podem ser definidas via variáveis de ambiente ou no arquivo `application-local.properties`:

| Variável | Valor Padrão | Descrição |
| :--- | :--- | :--- |
| `DB_HOST` | `localhost` | Endereço do servidor MySQL |
| `DB_PORT` | `3306` | Porta do MySQL |
| `DB_NAME` | `trilha_mais` | Nome do schema do banco de dados |
| `DB_USERNAME` | `root` | Usuário do banco |
| `DB_PASSWORD` | `(vazio)` | Senha de acesso ao banco |
| `JWT_SECRET` | `9a8b7c...` | Chave de assinatura criptográfica HMAC-SHA256 |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Tempo de expiração do token em milissegundos |
| `GROQ_API_KEY` | `(vazio)` | Chave de API gratuita da Groq Cloud |

### Como Executar a Aplicação
No diretório `backend/`:

```bash
# Executa o servidor de desenvolvimento Spring Boot na porta 8080
./mvnw spring-boot:run
```

### Como Executar os Testes Automatizados
```bash
# Executa toda a suíte de 70 testes com geração de relatório Surefire
./mvnw test
```

### Como Gerar o Pacote JAR Executável
```bash
# Empacota a aplicação em target/backend-0.0.1-SNAPSHOT.jar
./mvnw package -DskipTests
```

---

## 🧪 Cobertura de Testes Automatizados (70 Testes — 100% Sucesso)

- `BackendApplicationTests`: 1 teste de inicialização do contexto Spring.
- `AuthControllerTest`: 8 testes de segurança, token JWT e login.
- `UserControllerTest`: 5 testes de cadastro, unicidade e perfil do estudante.
- `SubjectControllerTest`: 7 testes de CRUD e multitenancy de matérias.
- `TopicControllerTest`: 8 testes de integridade hierárquica matéria/tópicos.
- `StudyMaterialControllerTest`: 8 testes de materiais didáticos.
- `AiControllerTest`: 7 testes de integração e contingência com Groq IA.
- `QuestionControllerTest`: 9 testes de validação psicométrica de alternativas.
- `QuizControllerTest`: 9 testes de geração anti-fraude, notas e submissão.
- `PerformanceControllerTest`: 6 testes de analytics, histórico e recomendações.
- `TrilhaMaisE2EFlowTest`: 2 testes simulando a jornada real completa do aluno no app mobile.

---

*UNINASSAU Caruaru — Laboratório de Empreendimentos Inovadores | 2026*
