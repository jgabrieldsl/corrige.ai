# Chat em Tempo Real - Corrige.AI

## Arquitetura do Chat

O sistema de chat permite que múltiplos usuários se comuniquem em tempo real através de uma arquitetura de 3 camadas:

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  Frontend   │◄───────►│   Backend   │◄───────►│  Servidor   │
│   (React)   │   HTTP  │ (Spring)    │   TCP   │   (Java)    │
└─────────────┘   SSE   └─────────────┘  Socket └─────────────┘
```

### Fluxo de Comunicação

1. **Usuário conecta**:
   - Frontend → POST `/api/test` → Backend
   - Backend → TCP Socket → Servidor (porta 3001)
   - Servidor retorna `socketId` e `totalUsuarios`
   - Backend abre stream SSE para receber mensagens

2. **Usuário envia mensagem**:
   - Frontend → POST `/api/chat/send` → Backend
   - Backend → `PedidoDeMensagem` → Servidor
   - Servidor faz **broadcast** para todos os usuários conectados

3. **Usuário recebe mensagem**:
   - Servidor → `MensagemChat` → Backend
   - Backend → SSE Event → Frontend
   - Frontend atualiza UI automaticamente

## Componentes

### Servidor (Java TCP)

**Classes de Comunicação:**
- `PedidoDeMensagem` - Pedido para enviar mensagem
- `MensagemChat` - Mensagem de chat com userId, userType, mensagem, timestamp

**Broadcast:**
```java
// SupervisorDeConexao.java
synchronized (this.usuarios) {
    for (Parceiro parceiro : this.usuarios) {
        parceiro.receba(chatMessage); // Envia para todos
    }
}
```

### Backend (Spring Boot)

**Endpoints:**
- `POST /api/test` - Conecta ao servidor
- `POST /api/chat/send` - Envia mensagem
  ```json
  {
    "socketId": "uuid",
    "mensagem": "Hello World"
  }
  ```
- `GET /api/chat/stream/{socketId}` - Stream SSE de mensagens

**SocketConnectionManager:**
- Gerencia pool de conexões persistentes
- Envia `PedidoDeMensagem` ao servidor
- Recebe `MensagemChat` e notifica listeners
- Usa SSE (Server-Sent Events) para push em tempo real

### Frontend (React + TypeScript)

**Estrutura:**
```
src/app/home/
├── models/
│   ├── ChatModel.ts          # Interfaces ChatMessage, SendMessageRequest
│   └── ConnectionModel.ts
├── services/
│   ├── ChatService.ts        # API calls + EventSource (SSE)
│   └── ConnectionService.ts
├── controllers/
│   ├── ChatController.ts     # Zustand state management
│   └── ConnectionController.ts
├── hooks/
│   ├── useChatHome.ts        # Hook que conecta ao SSE
│   └── useConnectionHome.ts
└── view.tsx                  # UI com ExpandableChat
```

**Componente de Chat:**
```tsx
const { messages, sendMessage } = useChatHome(socketId)

// Auto-conecta ao SSE quando socketId muda
useEffect(() => {
  if (socketId) {
    const eventSource = new EventSource(
      `http://localhost:8080/api/chat/stream/${socketId}`
    )
    eventSource.addEventListener('chat-message', handleMessage)
  }
}, [socketId])
```

## Teste do Chat

### 1. Inicie os serviços

```bash
# Terminal 1 - Servidor
cd servidor
mvn exec:java

# Terminal 2 - Backend  
cd backend
mvn spring-boot:run

# Terminal 3 - Frontend
cd frontend/corrige.ai
npm run dev
```

### 2. Teste com múltiplos usuários

1. Abra `http://localhost:5173` em **duas abas diferentes**
2. Clique em "Conectar" em ambas as abas
3. Veja o contador "Usuários conectados: 2"
4. Abra o chat (ícone no canto inferior)
5. Digite uma mensagem em uma aba
6. A mensagem aparecerá **instantaneamente** em todas as abas!

### 3. Verificando logs

**Servidor:**
```
[CHAT] Mensagem recebida de abc123: Olá pessoal!
[CHAT] Mensagem transmitida para 2 usuário(s)
```

**Backend:**
```
[CHAT-abc123] Mensagem recebida de abc123: Olá pessoal!
Mensagem de chat enviada do socketId: uuid-1
```

**Frontend (Console):**
```
SSE Event: chat-message
{userId: "abc123", mensagem: "Olá pessoal!", timestamp: 1730777777}
```

## Características Técnicas

### Server-Sent Events (SSE)
- Conexão HTTP persistente unidirecional (servidor → cliente)
- Mais simples que WebSocket para este caso de uso
- Reconexão automática em caso de falha
- Suporte nativo no navegador via `EventSource`

### Broadcast de Mensagens
- O servidor mantém lista de todos os `Parceiro` conectados
- Quando recebe `PedidoDeMensagem`, itera sobre todos os usuários
- Envia `MensagemChat` via `ObjectOutputStream`
- Backend recebe via `ObjectInputStream` e notifica SSE

### Thread Safety
- Lista de usuários sincronizada com `synchronized (this.usuarios)`
- Pool de conexões no backend usa `ConcurrentHashMap`
- Listeners de mensagens em `ArrayList` thread-safe

## Próximas Melhorias

- [ ] Salvar mensagens no MongoDB
- [ ] Autenticação de usuários
- [ ] Salas de chat separadas
- [ ] Notificações de "usuário está digitando..."
- [ ] Histórico de mensagens ao conectar
- [ ] Upload de arquivos/imagens
- [ ] Markdown/emojis nas mensagens

## Troubleshooting

**Mensagens não aparecem:**
- Verifique se o servidor está rodando na porta 3001
- Confirme que o backend iniciou sem erros
- Abra DevTools → Network → Verifique stream SSE ativo

**Erro "Connection refused":**
- Certifique-se que a ordem foi: Servidor → Backend → Frontend
- Verifique firewall/antivirus não está bloqueando porta 3001

**SSE desconecta:**
- Normal após ~30 minutos de inatividade
- O navegador reconecta automaticamente
- Mensagens são bufferizadas e enviadas na reconexão
