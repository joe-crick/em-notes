# EM Notes — local install & run (Linux/macOS).
#
#   make install   # prerequisites + dependencies + database (run this first)
#   make run       # start the app (API + web) in dev mode
#
# Run `make` or `make help` to list every target.

SHELL := /bin/bash
NODE_MIN := 20
PNPM_VERSION := 10.33.0
DB := apps/server/data/em-notes.sqlite

.DEFAULT_GOAL := help
.PHONY: help check deps db install build test dev run reset-password clean

help: ## List available targets
	@echo "EM Notes — available targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) \
	  | sort \
	  | awk 'BEGIN{FS=":.*?## "}{printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2}'

check: ## Verify prerequisites (Node >= 20, pnpm)
	@command -v node >/dev/null 2>&1 || { echo "✗ Node.js not found — install Node >= $(NODE_MIN): https://nodejs.org"; exit 1; }
	@major=$$(node -p 'process.versions.node.split(".")[0]'); \
	  if [ "$$major" -lt "$(NODE_MIN)" ]; then echo "✗ Node $$(node -v) found — need >= $(NODE_MIN)."; exit 1; fi; \
	  echo "✓ Node $$(node -v)"
	@if command -v pnpm >/dev/null 2>&1; then \
	  echo "✓ pnpm $$(pnpm -v)"; \
	elif command -v corepack >/dev/null 2>&1; then \
	  echo "… provisioning pnpm@$(PNPM_VERSION) via corepack"; \
	  corepack enable && corepack prepare pnpm@$(PNPM_VERSION) --activate && echo "✓ pnpm $$(pnpm -v)"; \
	else \
	  echo "✗ pnpm not found and corepack unavailable — install pnpm: https://pnpm.io/installation"; exit 1; \
	fi
	@echo "  (native deps better-sqlite3 + argon2 use prebuilt binaries; a fallback build needs python3 + a C/C++ toolchain.)"

deps: check ## Install workspace dependencies
	pnpm install

db: ## Create + migrate the SQLite database and load seed data
	pnpm db:migrate
	pnpm db:seed

install: deps db ## Full install: prerequisites, dependencies, seeded database
	@echo ""
	@echo "✓ EM Notes is installed. Start it with:  make run"
	@echo "  Then open the printed http://127.0.0.1:<port> URL and set a password on first run."

build: ## Production build of the web app
	pnpm build

test: ## Run the full test suite
	pnpm -r test

run: check ## Run the app (API + web) in dev mode
	pnpm dev

dev: run ## Alias for `run`

reset-password: ## Clear the local password (next launch re-runs first-time setup)
	pnpm --filter @em-notes/server auth:reset

clean: ## Remove dependencies, build output, and the local database (destructive)
	rm -rf node_modules apps/*/node_modules packages/*/node_modules
	rm -rf apps/web/dist
	rm -f $(DB) $(DB)-wal $(DB)-shm $(DB)-journal
