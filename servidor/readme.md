# Servidor de Chat - Corrige.AI

Servidor Java implementado com Sockets TCP para gerenciar comunicação de chat em tempo real. Funciona como hub de mensagens, permitindo broadcast entre múltiplos usuários conectados.

## Arquitetura Geral

```
Frontend ←→ Backend ←→ Servidor Socket
    ↓         ↓           ↓
   SSE    MongoDB    Broadcast TCP
```

### Componentes:
- **Frontend**: Interface React com chat em tempo real
- **Backend**: API Spring Boot que gerencia conexões e SSE
- **Servidor Socket**: Hub Java TCP para broadcast de mensagens
- **Database**: MongoDB para persistir dados de conexões

## Funcionalidades Implementadas

### Sistema de Chat
- **Conexões múltiplas**: Suporte a vários usuários simultâneos
- **Broadcast**: Mensagens enviadas para todos os conectados
- **Thread safety**: Operações seguras com múltiplas threads
- **Deduplicação**: Prevenção de mensagens duplicadas

### Comunicação
- **Protocolo**: TCP/IP na porta 3001
- **Persistência**: Conexões mantidas durante sessão
- **SSE**: Server-Sent Events para push em tempo real
- **MongoDB**: Armazenamento de dados de conexões

## Componentes Principais

### Classes do Servidor:
- **`ServidorTicket`**: Classe principal, inicia servidor na porta 3001
- **`AceitadoraDeConexao`**: Thread que aceita novas conexões TCP
- **`SupervisorDeConexao`**: Thread que gerencia cada cliente conectado
- **`Parceiro`**: Representa um cliente conectado (Backend)

### Classes de Comunicação:
- **`PedidoDeConexao`**: Solicitação de conexão do Backend
- **`RespostaDeConexao`**: Resposta com socketId e dados da conexão
- **`PedidoDeMensagem`**: Envio de mensagem para broadcast
- **`MensagemChat`**: Mensagem distribuída para todos os usuários

## Fluxo de Operação

### 1. Conexão:
- Frontend solicita conexão via Backend
- Backend conecta TCP com Servidor
- Servidor retorna socketId único
- Backend salva conexão no MongoDB

### 2. Chat:
- Usuário envia mensagem via Frontend
- Backend encaminha para Servidor via TCP
- Servidor faz broadcast para todos os conectados
- Backend recebe e distribui via SSE para Frontend

### 3. Desconexão:
- Cliente desconecta ou timeout
- Servidor remove da lista de usuários ativos
- Recursos são liberados automaticamente

## Como Executar

**Servidor:**
```bash
cd servidor
mvn clean compile exec:java
```

**Integração:**
- Servidor inicia na porta 3001
- Backend conecta automaticamente
- Frontend recebe mensagens via SSE

## Status do Projeto

### ✅ Implementado:
- Chat em tempo real funcional
- Broadcast de mensagens
- Múltiplas conexões simultâneas
- Persistência de conexões
- Thread safety

### 📋 Escopo Atual:
- Sistema de chat simples
- Comunicação básica entre usuários
- Arquitetura de 3 camadas funcionando