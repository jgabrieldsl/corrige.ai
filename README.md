# Correção de Redação ENEM com Inteligência Artificial
Este projeto desenvolve uma plataforma para correção automatizada de redações do ENEM utilizando Inteligência Artificial. A plataforma aborda desafios comuns na correção de redações, como a demora do processo manual, a ausência de feedback instantâneo e a inconsistência nas avaliações.

Utilizando APIs de IA para análise de texto, o sistema avalia competências como gramática, coesão, argumentação e proposta de intervenção, simulando os critérios oficiais do ENEM. O backend é implementado em Java para atender a requisitos acadêmicos, e o frontend é construído com React e TypeScript, garantindo uma interface de usuário moderna e responsiva.

## Funcionalidades
- **Correção Automatizada**: Envio de redações pela plataforma com feedback instantâneo baseado nas competências do ENEM.
- **Feedback Detalhado**: Análise e comentários sobre gramática, coesão, argumentação e sugestões de melhoria.
- **Relatórios de Progresso**: Acompanhamento do desempenho do usuário ao longo do tempo, com métricas e gráficos.
- **Interface Amigável**: Plataforma web responsiva e acessível.
- **Modo Freemium**: Versão gratuita com limitações e planos premium para acesso ilimitado.

## Tecnologias Utilizadas
- **Frontend**: React com TypeScript
- **Backend**: Java (Spring Boot para API RESTful)
- **Servidor Java**: Servidor de sockets para comunicação em tempo real
- **Banco de Dados**: MongoDB
- **IA**: Integração com APIs externas
- **Fluxo de Versionamento**: Gitflow
- **Testes**: Implementação de testes unitários e de integração (usando JUnit para Java e Jest para React)

## Como Rodar o Projeto


### 1. Servidor Java (Porta 3001)
O servidor Java gerencia conexões TCP em tempo real.

```bash
# Navegue até o diretório do servidor
cd servidor

# Compile e execute o servidor
mvn exec:java
```

**Comandos disponíveis no servidor:**
- `desativar` - Para desligar o servidor
- `status` - Exibe o status do servidor

O servidor estará rodando na porta **3001** e aguardando conexões.

---

### 2. Backend Spring Boot (Porta 8080)
O backend fornece a API REST e gerencia a comunicação entre o frontend e o servidor Java.

```bash
# Navegue até o diretório do backend
cd backend

# Compile e execute o Spring Boot
mvn spring-boot:run
```
---

### 3. Frontend React
O frontend é a interface do usuário.

```bash
# Navegue até o diretório do frontend
cd frontend/corrige.ai

# Instale as dependências (primeira vez)
yarn install

# Execute o servidor de desenvolvimento
yarn dev
```

O frontend estará disponível em: **http://localhost:5173**

---

### Ordem de Inicialização Recomendada
1. **Primeiro**: Inicie o Servidor Java (porta 3001)
2. **Segundo**: Inicie o Backend Spring Boot (porta 8080)
3. **Terceiro**: Inicie o Frontend React (porta 5173)

### Testando a Aplicação
1. Acesse o frontend no navegador
2. Clique em **"Conectar"**
3. Você verá:
   - Socket ID gerado
   - Timestamp da conexão
   - Total de usuários conectados
4. Abra outra aba e conecte novamente - o contador aumentará
5. Clique em **"Desconectar"** para fechar a conexão
---

## 2º Entrega - Sistema de Comunicação em Tempo Real

### Processo Selecionado: Socket TCP e Implementação do Chat

O sistema implementa uma arquitetura de comunicação em tempo real dividida em 3 camadas:

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  Frontend   │◄───────►│   Backend   │◄───────►│  Servidor   │
│   (React)   │   HTTP  │ (Spring)    │   TCP   │   (Java)    │
└─────────────┘   SSE   └─────────────┘  Socket └─────────────┘
```

### Fluxo de Conexão com Socket

1. **Estabelecimento de Conexão**:
   - Frontend → `POST /api/test` → Backend (porta 8080)
   - Backend abre conexão TCP persistente com Servidor (porta 3001)
   - Servidor gera `socketId` único (UUID) e retorna quantidade de usuários conectados
   - Backend armazena conexão no pool (`ConcurrentHashMap`)
   - Resposta retorna para Frontend com dados da conexão

2. **Manutenção da Conexão**:
   - Conexão TCP permanece aberta
   - Thread listener no Backend aguarda mensagens do Servidor
   - Pool de conexões gerenciado por `SocketConnectionManager`

3. **Desconexão**:
   - Frontend → `DELETE /api/disconnect/{socketId}` → Backend
   - Backend fecha socket TCP com Servidor
   - Remove conexão do pool
   - Servidor atualiza contador de usuários

### Sistema de Chat em Tempo Real

#### Arquitetura do Chat

O chat permite comunicação bidirecional entre múltiplos usuários através de:
- **Broadcast** no Servidor Java (TCP Sockets)
- **SSE (Server-Sent Events)** no Backend para push em tempo real
- **Deduplicação** de mensagens para evitar duplicatas

#### Fluxo de Envio de Mensagem

1. **Usuário envia mensagem**:
   - Frontend → `POST /api/chat/send` → Backend
   ```json
   {
     "socketId": "uuid-do-usuario",
     "mensagem": "Olá, pessoal!"
   }
   ```

2. **Backend processa e envia ao Servidor**:
   - Backend → `PedidoDeMensagem` → Servidor (via TCP Socket)
   - Servidor recebe e cria `MensagemChat` com timestamp

3. **Servidor faz Broadcast**:
   - Servidor itera sobre todos os `Parceiro` conectados
   - Envia `MensagemChat` para cada conexão TCP ativa
   ```java
   synchronized (this.usuarios) {
       for (Parceiro parceiro : this.usuarios) {
           parceiro.receba(chatMessage);
       }
   }
   ```

4. **Backend notifica clientes via SSE**:
   - Backend recebe mensagem em cada conexão TCP
   - Sistema de deduplicação (chave: `userId_timestamp`) evita duplicatas
   - Notifica todos os SSE emitters conectados
   - Frontend recebe evento `chat-message` instantaneamente

#### Fluxo de Recebimento em Tempo Real

1. **Frontend conecta ao stream SSE**:
   - Frontend → `GET /api/chat/stream/{socketId}` → Backend
   - Backend cria `SseEmitter` e registra
   - Conexão HTTP persistente fica aberta aguardando eventos

2. **Listener registrado uma única vez**:
   - `ChatController` usa double-checked locking
   - Listener global processa todas as mensagens recebidas
   - Envia para todos os SSE emitters ativos

3. **Frontend recebe e atualiza UI**:
   - EventSource recebe evento `chat-message`
   - Zustand state atualizado automaticamente
   - UI renderiza nova mensagem sem reload

### Rotas e Endpoints Expostos

#### Conexão
| Método | Endpoint | Descrição | Request Body | Response |
|--------|----------|-----------|--------------|----------|
| POST | `/api/test` | Estabelece conexão com servidor | `{ tipo: "CONNECT", dados: { userId, userType, authToken } }` | `{ tipo: "CONNECT_SUCCESS", dados: { socketId, timestamp, totalUsuarios } }` |
| GET | `/api/connections` | Lista todas as conexões ativas | - | `[{ socketId, timestamp, tipo, totalUsuarios }]` |
| DELETE | `/api/disconnect/{socketId}` | Desconecta um socket específico | - | `{ message: "Desconectado com sucesso" }` |

#### Chat
| Método | Endpoint | Descrição | Request Body | Response |
|--------|----------|-----------|--------------|----------|
| POST | `/api/chat/send` | Envia mensagem de chat | `{ socketId: "uuid", mensagem: "texto" }` | `{ message: "Mensagem enviada com sucesso" }` |
| GET | `/api/chat/stream/{socketId}` | Stream SSE para receber mensagens | - | EventSource stream com eventos `chat-message` |

#### Estrutura de Mensagens SSE
```javascript
// Evento recebido no frontend via SSE
event: chat-message
data: {
  "userId": "abc123",
  "userType": "STUDENT",
  "mensagem": "Olá, pessoal!",
  "timestamp": 1730777777000
}
```
---

## Contribuição
Este projeto utiliza o **Gitflow** como fluxo de trabalho para gerenciar branches. As branches principais são:
- `main`: Branch de produção.
- `develop`: Branch de desenvolvimento.

**Passos para Contribuir:**
1. Crie sua branch a partir da `develop`: `git checkout -b feat/sua-funcionalidade`.
2. Faça o commit de suas mudanças: `git commit -m "feat: descricao da nova funcionalidade"`.
3. Envie para o repositório: `git push -u origin feat/sua-funcionalidade`.
4. Abra um Pull Request para a branch `develop`.

## Autores
- Bruno Reitano
- Gabriel Bonatto
- João Gabriel

## Licença
Este projeto está licenciado sob a MIT License. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.