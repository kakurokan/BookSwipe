# BookSwipe

An app for discovering your next read. Instead of searching, you swipe through a deck of books: save the ones that interest you, reject the ones that don't, and mark the ones you've already read. The more you swipe, the better the recommendations get.

> **Status:** design phase. The documentation (user stories, wireframes, data model and API) is done; `backend/` and `frontend/` don't have code yet.

## How it works

| Gesture | Action |
| --- | --- |
| Swipe right | Save to the "to read" list |
| Swipe left | Not interested |
| Swipe up | Already read (with an optional 0.5 to 5 star rating) |
| Back | Undo the last swipe |

The shelf then organizes books into tabs (to read, reading, read, rejected). Book data comes from [Open Library](https://openlibrary.org/).

## Documentation

| Document | Contents |
| --- | --- |
| [docs/wireframes/](docs/wireframes/README.md) | Screens and user stories (US-01 to US-19) with acceptance criteria |
| [docs/er-diagram.md](docs/er-diagram.md) | Data model (Mermaid ER diagram) |
| [docs/api/openapi.yaml](docs/api/openapi.yaml) | OpenAPI 3.0 specification of the REST API (`/api/v1`) |

## Repository structure

```text
backend/     API (not implemented yet)
frontend/    Client app (not implemented yet)
docs/        Wireframes, ER diagram and API specification
learning/    Learning log (see CLAUDE.md)
.github/     Pull request template and workflows
```

## Running the database with Docker

Prerequisites: [Docker](https://docs.docker.com/get-docker/) with the Compose plugin (`docker compose`).

1. Create your local environment file from the example:

   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and set at least `POSTGRES_PASSWORD`. `POSTGRES_DB` and `POSTGRES_USER` default to `bookswipe`.

3. Start PostgreSQL in the background:

   ```bash
   docker compose up -d
   ```

4. Check that it is ready. The `db` service should show `healthy`:

   ```bash
   docker compose ps
   ```

PostgreSQL 17 is then available on **`localhost:5433`** (host port 5433, mapped to 5432 inside the container). To connect with `psql`:

```bash
psql -h localhost -p 5433 -U bookswipe -d bookswipe
```

Useful commands:

| Command | What it does |
| --- | --- |
| `docker compose logs -f db` | Follow the database logs |
| `docker compose stop` | Stop the container, keeping the data |
| `docker compose down` | Remove the container, keeping the data |
| `docker compose down -v` | Remove the container **and delete the data** (`pgdata` volume) |

Data is stored in the named volume `pgdata`, so it survives restarts and `docker compose down`.

> **Note:** the credentials are read from `.env` only when the volume is first created. If you change `POSTGRES_USER`, `POSTGRES_PASSWORD` or `POSTGRES_DB` later, run `docker compose down -v` to recreate the database with the new values.

## Contributing

- PR titles follow [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `chore:`…).
- If you change an endpoint, update `docs/api/openapi.yaml`.
- If you change the database, add a new Flyway migration; don't edit one that has already been applied.
- Don't include secrets (`.env`, keys, passwords). Use `.env.example` as a reference.

The PR template is at [.github/pull_request_template.md](.github/pull_request_template.md).

## License

[GNU AGPL v3](LICENSE)
