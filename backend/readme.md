# Backend - Corrige.ai

## Objetivo
Backend da plataforma Corrige.ai, desenvolvido em Java com Spring Boot, focado em resolver a dor do usuário relacionada à demora, ansiedade e falta de acesso a feedbacks de redação. O sistema atua como intermediário entre o aluno e a IA, garantindo análises rápidas e feedbacks detalhados das redações.

## Tecnologias Principais (MVP)
- Java
- Spring Boot
- Spring WebSocket (Comunicação em tempo real)
  - ```spring-boot-starter-websocket```
- MongoDB
- JUnit

## Estrutura do Projeto
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controllers/    # Controladores REST (endpoints da API)
│   │   │   ├── services/       # Regras de negócio
│   │   │   ├── models/         # Classes de domínio
│   │   │   ├── repositories/   # Acesso ao banco de dados
│   │   │   ├── config/         # Configurações
│   │   │   └── utils/          # Classes utilitárias
│   │   └── resources/          # Arquivos de configuração
│   └── test/                   # Testes unitários e de integração
```

## Configuração do Ambiente
1. Configure o JDK 17+
2. Configure o MongoDB
3. Configure as variáveis de ambiente no .env
4. Execute o projeto:

## Funcionalidades do MVP

### 1. Autenticação e Usuários
- `POST /api/auth/register`: Cadastro de novo usuário
- `POST /api/auth/login`: Login de usuário
- `GET /api/users/profile`: Obter perfil do usuário

### 2. Gestão de Redações (MVP)
- `POST /api/essays/text`: Enviar redação via texto
- `GET /api/essays`: Listar histórico de redações
- `GET /api/essays/{id}`: Obter redação específica
- `GET /api/essays/{id}/feedback`: Obter feedback detalhado

### 3. Sistema de Tickets
- `POST /api/tickets`: Criar ticket para comunicação com professor
- `GET /api/tickets`: Listar tickets do usuário
- `PUT /api/tickets/{id}`: Atualizar ticket

## Funcionalidades Futuras (Não obrigatórias)

### 1. Upload de Arquivos
- `POST /api/essays/file`: Enviar redação via arquivo (PDF/DOCX)

### 2. Sistema de Pagamento
- `GET /api/plans`: Listar planos disponíveis
- `POST /api/subscriptions`: Criar assinatura
- `GET /api/subscriptions/status`: Verificar status da assinatura

### 3. Gamificação
- `GET /api/challenges`: Listar desafios disponíveis
- `GET /api/rankings`: Obter rankings
- `GET /api/achievements`: Listar conquistas do usuário

## Integração com IA (MVP)
O backend integra com APIs de IA para análise das competências do ENEM:
1. Domínio da norma culta da língua escrita
2. Compreensão do tema e aplicação das áreas de conhecimento
3. Capacidade de organizar e relacionar informações e argumentos
4. Conhecimento dos mecanismos linguísticos necessários para a construção da argumentação
5. Elaboração de proposta de intervenção para o problema apresentado

### Processo de Análise
1. Recebimento do texto do frontend
2. Pré-processamento e formatação
3. Envio para API de IA
4. Processamento do feedback recebido
5. Armazenamento dos resultados
6. Envio do feedback estruturado para o frontend

### Visão Geral da Comunicação com o Servidor
O backend atua como ponte entre o frontend e o servidor de tickets Java, gerenciando toda a comunicação relacionada ao sistema de dúvidas e interação aluno-professor.

### Fluxo de Comunicação
1. Frontend -> Backend:
   - Cliente faz requisição HTTP REST
   - Backend valida a requisição
   - Backend prepara os dados para o servidor

2. Backend -> Servidor:
   - Conexão via WebSocket
   - Envio de mensagens em tempo real
   - Gerenciamento de estado da conexão

3. Servidor -> Backend:
   - Recebimento de respostas em tempo real
   - Atualização de status dos tickets
   - Notificações de novas mensagens
   - Confirmação de entrega

### Componentes Principais
1. TicketController:
   - Recebe requisições do frontend
   - Valida permissões e dados
   - Coordena comunicação com servidor

2. WebSocketService:
   - Mantém conexão com servidor
   - Gerencia reconexões
   - Processa mensagens recebidas

3. TicketService:
   - Lógica de negócio dos tickets
   - Sincronização com banco de dados
   - Cache de mensagens recentes

### Requisitos de Comunicação
1. Conexão:
   - Estabelecimento de conexão WebSocket
   - Autenticação do cliente
   - Reconexão automática em falhas

2. Mensagens:
   - Envio em tempo real
   - Confirmação de entrega
   - Buffer para falhas de conexão

3. Estado:
   - Monitoramento de conexão
   - Sincronização de estado
   - Recuperação de falhas