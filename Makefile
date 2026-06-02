COMPOSE = docker compose -f .docker/docker-compose.yml

.PHONY: up down build migrate shell logs ps

up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

build:
	$(COMPOSE) build

migrate:
	$(COMPOSE) exec dev gradle flywayMigrate --no-daemon

shell:
	$(COMPOSE) exec dev sh

logs:
	$(COMPOSE) logs -f app

ps:
	$(COMPOSE) ps
