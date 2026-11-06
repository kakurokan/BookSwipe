# What changes

<!-- One or two sentences: what was done and why. -->

Closes #

## How to test

<!-- Steps for the reviewer to confirm it works. Example:
1. `docker compose up -d`
2. `docker compose ps` shows `healthy`
-->

## Checklist

- [ ] The title follows Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`…)
- [ ] I tested locally and it works
- [ ] If I changed an endpoint, I updated `docs/api/openapi.yaml`
- [ ] If I changed the database, I added a new Flyway migration (did not edit an applied one)
- [ ] I did not include secrets (`.env`, keys, passwords)
- [ ] CI passes

## Notes for the reviewer

<!-- Optional: open questions, decisions made, points that need attention, screenshots. -->