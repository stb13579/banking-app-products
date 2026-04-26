# banking-app-products — Products Service

Java 17 / Spring Boot 3 products service. Manages the product catalog and handles credit card and loan applications.

Part of the mock consumer banking application.

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
mvn spring-boot:run
```

---

## API

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/products` | Bearer | Product catalog |
| `POST` | `/products/credit-card/apply` | Bearer | Apply for credit card |
| `POST` | `/products/loan/apply` | Bearer | Apply for loan |
| `GET` | `/applications` | Bearer | Your applications |
| `GET` | `/applications/:id` | Bearer | Get application by ID |
| `POST` | `/admin/products/import` | Bearer (admin) | Import products from XML |

---

## curl Examples

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

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/banking` | PostgreSQL connection string |
| `SPRING_DATASOURCE_USERNAME` | `banking` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | `banking` | Database password |
| `JWT_SECRET` | `supersecret123` | JWT verification secret |
| `SERVER_PORT` | `8003` | Service port |
