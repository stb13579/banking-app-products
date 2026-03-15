# banking-app-products — Products Service

> Part of the **Mock Consumer Banking App** — a security demo for [Aikido Security](https://www.aikido.dev).

Node.js 20 / NestJS products service. Manages the product catalog and credit card / loan applications. **Intentionally vulnerable** — findings surface in Aikido SAST, secrets detection, and SCA scanning.

---

## Intentional Vulnerabilities

| Vulnerability | Location | Aikido Category |
|--------------|----------|-----------------|
| Hardcoded credit bureau API key | `src/products/products.service.ts:9` | Secrets Detection |
| IDOR on `GET /applications/:id` | `src/applications/applications.controller.ts:getOne` | SAST |
| `axios@0.21.1` — CVE-2021-3749 (SSRF) | `package.json` | SCA |

---

## Quick Start

### Standalone (with Docker)

```bash
docker compose up
```

Service starts on port **8003**. Swagger UI at http://localhost:8003/api.

### Local development

```bash
cp .env.example .env
npm install
npm run build
npm run start:prod
```

---

## API

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/products` | Bearer | Product catalog |
| `POST` | `/products/credit-card/apply` | Bearer | Apply for credit card |
| `POST` | `/products/loan/apply` | Bearer | Apply for loan |
| `GET` | `/applications` | Bearer | Your applications |
| `GET` | `/applications/:id` | Bearer | **[VULN IDOR]** Get application by ID |

---

## curl Examples

```bash
TOKEN=<your_jwt>
```

### Browse products

```bash
curl -s http://localhost:8003/products \
  -H "Authorization: Bearer $TOKEN" | jq
```

### Apply for credit card

```bash
curl -s -X POST http://localhost:8003/products/credit-card/apply \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"annual_income":75000,"employment_status":"employed"}' | jq
```

### Apply for loan

```bash
curl -s -X POST http://localhost:8003/products/loan/apply \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":10000,"purpose":"home improvement","annual_income":75000}' | jq
```

### IDOR demo — fetch any application

```bash
curl -s http://localhost:8003/applications/<ANY_APPLICATION_ID> \
  -H "Authorization: Bearer $TOKEN" | jq
```

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `postgresql://banking:banking@localhost:5432/banking` | PostgreSQL connection string |
| `JWT_SECRET` | `supersecret123` | JWT verification secret |
| `PORT` | `8003` | Service port |
