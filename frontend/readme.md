# Frontend - Corrige.ai
Frontend da plataforma Corrige.ai, desenvolvido com React e TypeScript. Esta parte do projeto é responsável pela interface do usuário, fornecendo uma experiência moderna e responsiva para a correção automatizada de redações.

## Tecnologias
- React
- TypeScript
- Tailwind
- Axios

## Comunicação com Backend

#### Envio de Redação:
1. Frontend envia texto via POST
2. Backend processa e envia para IA
3. IA retorna ao Backend que retorna ao Frontend
4. Atualização automática ao receber resultado

#### Sistema de Tickets:
1. Conexão WebSocket
2. Inscrição em canais específicos por ticket
3. Atualizações em tempo real de mensagens
4. Reconexão automática em caso de falhas