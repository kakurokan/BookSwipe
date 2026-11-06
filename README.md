# BookSwipe

Uma app para descobrires a tua próxima leitura. Em vez de pesquisar, fazes *swipe* num baralho de livros: guardas os que te interessam, rejeitas os que não, e marcas os que já leste. Quanto mais fazes swipe, melhores ficam as recomendações.

> **Estado:** em fase de design. A documentação (histórias, wireframes, modelo de dados e API) está feita; o `backend/` e o `frontend/` ainda não têm código.

## Como funciona

| Gesto | Ação |
| --- | --- |
| Swipe para a direita | Guardar na lista "para ler" |
| Swipe para a esquerda | Não me interessa |
| Swipe para cima | Já li (com nota opcional de 0,5 a 5 estrelas) |
| Voltar | Desfazer o último swipe |

Depois, a estante organiza os livros por separadores (para ler, a ler, lidos, rejeitados). Os dados dos livros vêm da [Open Library](https://openlibrary.org/).

## Documentação

| Documento | Conteúdo |
| --- | --- |
| [docs/wireframes/](docs/wireframes/README.md) | Ecrãs e histórias de utilizador (US-01 a US-19) com critérios de aceitação |
| [docs/er-diagram.md](docs/er-diagram.md) | Modelo de dados (diagrama ER em Mermaid) |
| [docs/api/openapi.yaml](docs/api/openapi.yaml) | Especificação OpenAPI 3.0 da API REST (`/api/v1`) |

## Estrutura do repositório

```text
backend/     API (por implementar)
frontend/    App cliente (por implementar)
docs/        Wireframes, diagrama ER e especificação da API
learning/    Registo de aprendizagem (ver CLAUDE.md)
.github/     Template de pull request e workflows
```

## Contribuir

- Os títulos dos PRs seguem [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `chore:`…).
- Se alterares um endpoint, atualiza `docs/api/openapi.yaml`.
- Se alterares a base de dados, cria uma nova migração Flyway; não edites uma já aplicada.
- Não incluas segredos (`.env`, chaves, palavras-passe). Usa `.env.example` como referência.

O template do PR está em [.github/pull_request_template.md](.github/pull_request_template.md).

## Licença

[GNU AGPL v3](LICENSE)
