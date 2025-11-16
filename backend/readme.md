# Backend - Corrige.ai
Backend da plataforma Corrige.ai, desenvolvido em Java com Spring Boot, focado em resolver a dor do usuário relacionada à demora, ansiedade e falta de acesso a feedbacks de redação. O sistema atua como intermediário entre o aluno e a IA, garantindo análises rápidas e feedbacks detalhados das redações.

## Tecnologias Principais (MVP)
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Web** (REST API + SSE)
- **Spring Data MongoDB** (Persistência)
- **Lombok** (Redução de boilerplate)
- **Maven** (Gerenciamento de dependências)


## Estrutura do Projeto
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/corrigeai/api/
│   │   │       ├── controllers/     # REST Controllers
│   │   │       │   ├── ChatController.java
│   │   │       │   └── ConnectionController.java
│   │   │       ├── services/        # Lógica de negócio
│   │   │       │   ├── SocketConnectionManager.java
│   │   │       │   ├── ServerCommunicationService.java
│   │   │       │   ├── SocketClientService.java
│   │   │       │   └── SocketService.java
│   │   │       ├── models/          # Entidades
│   │   │       │   ├── ConnectRequest.java
│   │   │       │   ├── ConnectResponse.java
│   │   │       │   └── SocketResponse.java
│   │   │       ├── repositories/    # Acesso MongoDB
│   │   │       │   └── SocketResponseRepository.java
│   │   │       ├── config/          # Configurações
│   │   │       │   ├── CorsConfig.java
│   │   │       │   └── JacksonConfig.java
│   │   │       └── CorrigeAiApplication.java
│   │   └── resources/
│   │       └── application.yml      # Configuração MongoDB
│   └── test/                        # Testes
└── pom.xml
```

### Executar o projeto
```bash
cd backend

# Via Maven
mvn spring-boot:run

# Ou compilar e executar
mvn clean compile
mvn exec:java -Dexec.mainClass="com.corrigeai.api.CorrigeAiApplication"
```

## Funcionalidades Implementadas (MVP)

### 1. Sistema de Conexão Socket
- `POST /api/connect`: Estabelece conexão com servidor socket
  ```json
  {
    "dados": {
      "userId": "user123",
      "userType": "Aluno", 
      "authToken": "token"
    }
  }
  ```
- `GET /api/connections`: Lista todas as conexões salvas no MongoDB
- `DELETE /api/connections/{socketId}`: Desconecta socket específico

### Sistema de Chat em Tempo Real
- `POST /api/chat/send`: Envia mensagem de chat
  ```json
  {
    "socketId": "uuid-123",
    "mensagem": "Olá pessoal!"
  }
  ```
- `GET /api/chat/stream/{socketId}`: Stream SSE para receber mensagens
  - Retorna eventos `chat-message` com dados da mensagem
  - Conexão persistente para push em tempo real

### Persistência MongoDB
- **Collection**: `socket_responses` - Dados de conexões
- **Modelo**: `SocketResponse` com socketId, timestamp, tipo, totalUsuarios
- **Repository**: `SocketResponseRepository` usando Spring Data MongoDB

## Arquitetura de Comunicação

### 1. Conexão de Usuário
1. **Frontend** → `POST /api/connect` → **Backend**
2. **Backend** → TCP Socket → **Servidor** (porta 3001)
3. **Servidor** retorna `RespostaDeConexao` com socketId
4. **Backend** salva no MongoDB e retorna dados para Frontend

### 2. Chat em Tempo Real
1. **Frontend** → `POST /api/chat/send` → **Backend**
2. **Backend** → `PedidoDeMensagem` → **Servidor**
3. **Servidor** faz broadcast da `MensagemChat` para todos
4. **Backend** recebe mensagem e envia via SSE para Frontend

### 3. Server-Sent Events (SSE)
- **Endpoint**: `GET /api/chat/stream/{socketId}`
- **Content-Type**: `text/event-stream`
- **Event Type**: `chat-message`
- **Deduplicação**: Sistema previne mensagens duplicadas
- **Thread Safety**: Gerenciamento seguro de múltiplos emitters

### Componentes Principais

#### 1. `SocketConnectionManager`
```java
@Service
public class SocketConnectionManager {
    // Pool de conexões TCP persistentes
    private final Map<String, PersistentConnection> connections;
    
    // Cache para deduplicação de mensagens
    private final Map<String, Long> processedMessages;
    
    public RespostaDeConexao connect(String userId, String userType, String authToken);
    public void sendChatMessage(String socketId, String mensagem);
    public void addChatMessageListener(Consumer<MensagemChat> listener);
}
```

#### 2. `ChatController`
```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    // Mapa de emitters SSE por socketId
    private final Map<String, SseEmitter> emitters;
    
    @GetMapping(value = "/stream/{socketId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@PathVariable String socketId);
    
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request);
}
```

#### 3. `ServerCommunicationService`
```java
@Service
public class ServerCommunicationService {
    public ConnectResponse handleConnection(ConnectRequest request) {
        // 1. Conecta com servidor socket
        // 2. Salva resposta no MongoDB
        // 3. Retorna dados para o frontend
    }
}
```

---