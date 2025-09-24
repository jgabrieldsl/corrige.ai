# Correção de Redação ENEM com Inteligência Artificial

## Descrição
Este projeto desenvolve uma plataforma para correção automatizada de redações do ENEM utilizando Inteligência Artificial. A plataforma aborda desafios comuns na correção de redações, como a demora do processo manual, a ausência de feedback instantâneo e a inconsistência nas avaliações.

Utilizando APIs de IA para análise de texto, o sistema avalia competências como gramática, coesão, argumentação e proposta de intervenção, simulando os critérios oficiais do ENEM. O backend é implementado em Java para atender a requisitos acadêmicos, e o frontend é construído com React e TypeScript, garantindo uma interface de usuário moderna e responsiva.

**Principais Limitações**: O sistema depende de APIs de IA pagas (ex: Grok, veja https://x.ai/api) e, portanto, está sujeito a custos e limites de uso em planos gratuitos.

## Funcionalidades
- **Correção Automatizada**: Envio de redações pela plataforma com feedback instantâneo baseado nas competências do ENEM.
- **Feedback Detalhado**: Análise e comentários sobre gramática, coesão, argumentação e sugestões de melhoria.
- **Relatórios de Progresso**: Acompanhamento do desempenho do usuário ao longo do tempo, com métricas e gráficos.
- **Interface Amigável**: Plataforma web responsiva e acessível.
- **Modo Freemium**: Versão gratuita com limitações e planos premium para acesso ilimitado.

## Tecnologias Utilizadas
- **Frontend**: React com TypeScript
- **Backend**: Java com Spring Boot (API RESTful) e Sockets (comunicação em tempo real)
- **Banco de Dados**: MongoDB
- **Inteligência Artificial**: Integração com APIs de terceiros (ex: Grok)
- **Controle de Versão**: Git com fluxo Gitflow
- **Testes**: JUnit (Java) e Jest (React)


## Contribuição
Este projeto utiliza o **Gitflow** como fluxo de trabalho para gerenciar branches. As branches principais são:
- `main`: Branch de produção.
- `develop`: Branch de desenvolvimento.

**Passos para Contribuir:**
1. Crie sua branch a partir da `develop`: `git checkout -b feature/sua-funcionalidade develop`.
2. Faça o commit de suas mudanças: `git commit -m "feat: Descrição da nova funcionalidade"`.
3. Envie para o repositório: `git push origin feature/sua-funcionalidade`.
4. Abra um Pull Request para a branch `develop`.

## Testes
Para garantir a qualidade do software, implementamos uma suíte de testes, atendendo também a requisitos da disciplina de Qualidade e Teste de Software.

- **Testes Unitários**: Cobertura de componentes React com Jest e lógica de negócio em Java com JUnit.
- **Testes de Integração**: Verificação dos endpoints da API e da comunicação com o banco de dados.

**Como Executar os Testes:**
```bash
# Para o frontend (no diretório 'frontend')
yarn test

# Para o backend (no diretório 'backend')
mvn test
```

## Autores
- Bruno Reitano
- Gabriel Bonatto
- João Gabriel

## Licença
Este projeto está licenciado sob a MIT License. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.