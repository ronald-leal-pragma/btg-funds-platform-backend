# BTG Funds — Backend

API REST para la plataforma de gestión de fondos de inversión BTG Pactual (FPV/FIC). Permite a un cliente registrarse, autenticarse, suscribirse a fondos, cancelar suscripciones, consultar su balance y ver el historial de transacciones con notificaciones vía email (SES) o SMS (SNS).

---

## Tecnologías

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 | Lenguaje principal (Records, Pattern Matching) |
| Spring Boot | 3.2.3 | Framework web y contenedor |
| Spring Data MongoDB | 3.2.3 | Persistencia local (perfil `!aws`) |
| AWS DynamoDB | — | Persistencia en nube (perfil `aws`) |
| AWS SES | — | Notificaciones por email (perfil `aws`) |
| AWS SNS | — | Notificaciones por SMS (perfil `aws`) |
| AWS Lambda | — | Runtime serverless (handler incluido) |
| Maven | 3.9+ | Build tool y gestión de dependencias |
| Lombok | 1.18.34 | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.3.0 | Documentación Swagger automática |
| JUnit 5 + Mockito | 5.x | Tests unitarios (81 tests, cobertura ≥ 80%) |
| JaCoCo | 0.8.11 | Análisis de cobertura de código |

---

## Arquitectura

El proyecto aplica **Clean Architecture + DDD** con separación estricta de capas:

```
src/main/java/com/btg/funds/
│
├── domain/                          # Núcleo — sin dependencias externas
│   ├── model/                       # Entidades inmutables (Java Records)
│   │   ├── Client.java              # Balance, preferencia de notificación, fondos activos, email/password
│   │   ├── Fund.java                # Id, nombre, monto mínimo, categoría
│   │   └── Transaction.java         # UUID, tipo (APERTURA/CANCELACION), timestamp
│   ├── repository/                  # Interfaces (ports) de repositorio
│   │   ├── ClientRepository.java
│   │   ├── FundRepository.java
│   │   └── TransactionRepository.java
│   └── exception/
│       ├── FundDomainException.java
│       ├── FondoNoEncontradoException.java
│       └── SaldoInsuficienteException.java
│
├── application/                     # Casos de uso — orquesta el dominio
│   ├── usecase/
│   │   ├── CreateClientUseCase.java
│   │   ├── LoginUseCase.java
│   │   ├── GetClientUseCase.java
│   │   ├── GetFundsUseCase.java
│   │   ├── SubscribeFundUseCase.java
│   │   ├── CancelFundUseCase.java
│   │   └── GetTransactionsUseCase.java
│   ├── port/
│   │   ├── in/                      # Interfaces de entrada (un puerto por caso de uso)
│   │   └── out/
│   │       └── NotificationPort.java
│   ├── dto/                         # Request / Response DTOs
│   └── mapper/                      # Mapeo dominio ↔ DTO
│
├── infrastructure/                  # Implementaciones concretas (adapters)
│   ├── persistence/
│   │   ├── document/                # @Document (MongoDB — perfil !aws)
│   │   ├── item/                    # @DynamoDbBean (DynamoDB — perfil aws)
│   │   ├── mapper/                  # Mappers documento/item ↔ dominio
│   │   └── repository/              # MongoXRepository + DynamoDbXRepository + SpringXRepository
│   ├── notification/
│   │   ├── AwsNotificationAdapter.java   # SES + SNS (perfil aws)
│   │   └── LogNotificationAdapter.java   # Logs (perfil !aws)
│   ├── lambda/
│   │   └── StreamLambdaHandler.java     # Entry point para AWS Lambda
│   └── config/
│       ├── AwsConfig.java           # Beans SES/SNS (perfil aws)
│       ├── DynamoDbConfig.java      # Bean DynamoDbEnhancedClient (perfil aws)
│       └── DataSeeder.java          # Datos semilla al arrancar
│
└── presentation/                    # Capa HTTP
    ├── controller/
    │   ├── ClientController.java    # /api/v1/client
    │   ├── FundController.java      # /api/v1/funds
    │   └── TransactionController.java  # /api/v1/transactions
    └── advice/
        └── GlobalExceptionHandler.java
```

---

## Perfiles de Spring

| Perfil | Base de datos | Notificaciones | Uso |
|---|---|---|---|
| `!aws` (default) | MongoDB local | Logs | Desarrollo local |
| `aws` | DynamoDB | SES + SNS | Producción / AWS |

---

## Requisitos previos (local)

- **Java 21** (recomendado: Eclipse Temurin)
- **Maven 3.9+**
- **MongoDB** en `localhost:27017` con autenticación **O** DynamoDB Local en `localhost:8000`

```bash
java -version   # 21.x
mvn -version    # 3.9+
```

```bash
# Mac con múltiples JDKs:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### Levantar MongoDB con Docker

```bash
docker run -d \
  --name mongodb-btg \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password123 \
  mongo:7
```

---

## Ejecución local

```bash
# Compilar
mvn clean compile

# Ejecutar (perfil !aws → usa MongoDB)
mvn spring-boot:run
```

El servidor inicia en **http://localhost:8081**.

Al arrancar, `DataSeeder` carga automáticamente:
- 5 fondos de inversión (FPV y FIC)
- 1 cliente demo: `user@email.com` / `btg1234` con balance inicial **COP $500,000**

---

## Endpoints

### Autenticación / Cliente

| Método | URL | Descripción |
|--------|-----|-------------|
| `POST` | `/api/v1/client/login` | Iniciar sesión (retorna datos del cliente) |
| `POST` | `/api/v1/client` | Crear nuevo cliente |
| `GET` | `/api/v1/client/{clientId}` | Estado del cliente (balance, fondos activos) |

### Fondos

| Método | URL | Parámetros | Descripción |
|--------|-----|-----------|-------------|
| `GET` | `/api/v1/funds` | `?clientId=` (opcional) | Listar fondos con estado de suscripción |
| `POST` | `/api/v1/funds/{id}/subscribe` | `?clientId=` | Suscribirse a un fondo |
| `DELETE` | `/api/v1/funds/{id}/cancel` | `?clientId=` | Cancelar suscripción |

### Transacciones

| Método | URL | Parámetros | Descripción |
|--------|-----|-----------|-------------|
| `GET` | `/api/v1/transactions` | `?clientId=` | Historial de transacciones |

### Ejemplos con curl

```bash
# Login
curl -X POST http://localhost:8081/api/v1/client/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@email.com","password":"btg1234"}'

# Crear cliente
curl -X POST http://localhost:8081/api/v1/client \
  -H "Content-Type: application/json" \
  -d '{"email":"nuevo@email.com","password":"pass123","notificationPreference":"email","contactInfo":"nuevo@email.com"}'

# Listar fondos con estado de suscripción
curl "http://localhost:8081/api/v1/funds?clientId=1"

# Suscribirse al fondo 1 (FPV_BTG_PACTUAL_RECAUDADORA — $75,000)
curl -X POST "http://localhost:8081/api/v1/funds/1/subscribe?clientId=1"

# Cancelar suscripción al fondo 1
curl -X DELETE "http://localhost:8081/api/v1/funds/1/cancel?clientId=1"

# Ver historial de transacciones
curl "http://localhost:8081/api/v1/transactions?clientId=1"
```

### Reglas de negocio

- Balance inicial: **COP $500,000**
- Saldo insuficiente → `HTTP 400`:
  ```json
  { "message": "No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA" }
  ```
- Cancelar un fondo **devuelve** el monto al balance
- Cada transacción genera un **UUID** único
- Al suscribirse se envía notificación según preferencia (`email` → SES, `sms` → SNS)

---

## Fondos disponibles (datos semilla)

| Id | Nombre | Monto Mínimo | Categoría |
|----|--------|-------------|-----------|
| 1 | FPV_BTG_PACTUAL_RECAUDADORA | COP $75,000 | FPV |
| 2 | FPV_BTG_PACTUAL_ECOPETROL | COP $125,000 | FPV |
| 3 | DEUDAPRIVADA | COP $50,000 | FIC |
| 4 | FDO-ACCIONES | COP $250,000 | FIC |
| 5 | FPV_BTG_PACTUAL_DINAMICA | COP $100,000 | FPV |

---

## Documentación Swagger

```
http://localhost:8081/swagger-ui.html   # UI interactiva
http://localhost:8081/api-docs          # Especificación OpenAPI JSON
```

---

## Tests

```bash
# Tests unitarios
mvn test

# Tests + reporte de cobertura (target/site/jacoco/index.html)
mvn verify
```

| Capa | Cobertura |
|------|-----------|
| Domain — `Client` | Métodos de balance y suscripción |
| Application — Use Cases | Caso feliz + errores de negocio |
| Infrastructure — Repositories | DynamoDB + MongoDB |
| Infrastructure — Notification | Email y SMS |
| Presentation — Controllers | HTTP 200 / 400 / 500 |
| Presentation — ExceptionHandler | Excepciones de dominio y genéricas |

**Total: 81 tests, 0 failures — cobertura mínima 80%**

---

## Build para producción

```bash
mvn clean package -DskipTests
```

Genera `target/btg-funds-platform-backend-0.0.1-SNAPSHOT.jar` (fat JAR vía `maven-shade-plugin`).

---

## Despliegue en AWS

El repositorio incluye un script de deploy automatizado y la plantilla CloudFormation.

### Prerequisitos

1. **AWS CLI** instalada y configurada (`aws configure`)
2. **Email SES verificado** — ir a [AWS Console → SES → Verified identities](https://console.aws.amazon.com/ses/home#/verified-identities) y verificar el email remitente. En sandbox, el destinatario también debe estar verificado.
3. **SNS SMS sandbox** — para probar SMS, verificar el número en [SNS → SMS sandbox](https://console.aws.amazon.com/sns/v3/home#/mobile/text-messages).

### Deploy completo (primera vez)

```bash
./deploy.sh --sender-email tu-email-verificado@gmail.com
```

### Opciones del script

```bash
./deploy.sh [opciones]

  --stack-name    Nombre del stack CloudFormation  (default: btg-funds-prod)
  --bucket        Bucket S3 para el JAR            (default: btg-funds-deploy-<account-id>)
  --region        Región AWS                        (default: us-east-1)
  --env           Ambiente (dev|staging|prod)        (default: prod)
  --sender-email  Email verificado en SES            (default: noreply@btgfunds.com)
  --skip-build    Omitir compilación del backend
  --skip-frontend Omitir build y sync del frontend
```

### Lo que hace el script

1. Crea el bucket S3 de deploy si no existe
2. Crea las tablas DynamoDB (`Clients`, `Funds`, `Transactions`) si no existen
3. Compila el backend (`mvn clean package -DskipTests`)
4. Sube el JAR a S3
5. Despliega el stack CloudFormation (`cloudformation/template.yml`)
6. Compila el frontend React (`npm run build`)
7. Sincroniza el frontend al bucket S3
8. Invalida la caché de CloudFront

### Infraestructura creada por CloudFormation

| Recurso | Descripción |
|---------|-------------|
| Lambda | Spring Boot como función Lambda (Java 21, 1024 MB) |
| API Gateway REST | Proxy hacia Lambda — expone `/api/v1/*` |
| DynamoDB | Tablas `Clients`, `Funds`, `Transactions` (PAY_PER_REQUEST) |
| S3 + CloudFront | Hosting del frontend React con HTTPS |
| IAM Role | Permisos mínimos (DynamoDB + SES + SNS + CloudWatch) |

### Outputs del stack

```bash
aws cloudformation describe-stacks --stack-name btg-funds-prod \
  --query 'Stacks[0].Outputs'
```

| Output | Descripción |
|--------|-------------|
| `ApiEndpoint` | URL base del API Gateway |
| `CloudFrontDomain` | URL pública del frontend |
| `FrontendBucketName` | Bucket S3 del frontend |
| `CloudFrontDistributionId` | ID de la distribución CloudFront |

### Credenciales demo (tras primer deploy)

```
Email   : user@email.com
Password: btg1234
```
