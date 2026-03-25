# BTG Funds — Backend

API REST para la plataforma de gestión de fondos de inversión BTG Pactual (FPV/FIC). Permite a un cliente suscribirse a fondos, cancelar suscripciones, consultar su balance y ver el historial de transacciones.

---

## Tecnologías

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 | Lenguaje principal (Records, Pattern Matching) |
| Spring Boot | 3.2.3 | Framework web y contenedor |
| Spring Data MongoDB | 3.2.3 | Persistencia (ODM) |
| MongoDB | 8.x | Base de datos NoSQL |
| Maven | 3.9+ | Build tool y gestión de dependencias |
| Lombok | 1.18.34 | Reducción de boilerplate |
| MapStruct | 1.5.5 | Mapeo de DTOs |
| SpringDoc OpenAPI | 2.3.0 | Documentación Swagger automática |
| JUnit 5 | 5.10 | Framework de tests unitarios |
| Mockito | 5.x | Mocks para tests |
| JaCoCo | 0.8.11 | Análisis de cobertura de código (mín. 80%) |

---

## Arquitectura

El proyecto aplica **Clean Architecture + DDD** con separación estricta de capas:

```
src/main/java/com/btg/funds/
│
├── domain/                          # Núcleo — sin dependencias externas
│   ├── model/                       # Entidades inmutables (Java Records)
│   │   ├── Client.java              # Balance, preferencia de notificación, fondos activos
│   │   ├── Fund.java                # Id, nombre, monto mínimo, categoría
│   │   └── Transaction.java         # UUID, tipo (APERTURA/CANCELACION), timestamp
│   ├── repository/                  # Interfaces (ports) de repositorio
│   │   ├── ClientRepository.java
│   │   ├── FundRepository.java
│   │   └── TransactionRepository.java
│   └── service/
│       └── FundDomainException.java # Excepción de negocio personalizada
│
├── application/                     # Casos de uso — orquesta el dominio
│   ├── usecase/
│   │   ├── SubscribeFundUseCase.java
│   │   ├── CancelFundUseCase.java
│   │   ├── GetFundsUseCase.java
│   │   ├── GetTransactionsUseCase.java
│   │   └── GetClientUseCase.java
│   └── port/
│       └── NotificationPort.java    # Interfaz de notificación (email/SMS)
│
├── infrastructure/                  # Implementaciones concretas (adapters)
│   ├── persistence/                 # Repositorios MongoDB
│   │   ├── document/                # Documentos @Document (MongoDB)
│   │   ├── MongoClientRepository.java
│   │   ├── MongoFundRepository.java
│   │   └── MongoTransactionRepository.java
│   ├── notification/
│   │   └── LogNotificationAdapter.java  # Notificación via logs (stub)
│   └── config/
│       └── DataSeeder.java          # Datos semilla iniciales
│
└── presentation/                    # Capa HTTP
    ├── controller/
    │   ├── FundController.java
    │   ├── TransactionController.java
    │   └── ClientController.java
    └── advice/
        └── GlobalExceptionHandler.java  # Manejo global de errores
```

---

## Requisitos previos

- **Java 21** (recomendado: Eclipse Temurin)
- **Maven 3.9+**
- **MongoDB** corriendo en `localhost:27017` con autenticación

### Verificar versiones

```bash
java -version        # debe mostrar 21.x
mvn -version         # debe mostrar 3.9+
```

### Verificar JAVA_HOME (importante en Mac con múltiples JDKs)

```bash
# En Mac con brew / múltiples JDKs, forzar Java 21:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

---

## Configuración de MongoDB

La aplicación requiere MongoDB con autenticación. Configura la URI en `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://admin:password123@localhost:27017/btgfunds_db?authSource=admin}
```

Para sobreescribir en tiempo de ejecución usa la variable de entorno:

```bash
export MONGODB_URI="mongodb://tu_usuario:tu_password@localhost:27017/btgfunds_db?authSource=admin"
```

### Levantar MongoDB con Docker (si no tienes una instancia activa)

```bash
docker run -d \
  --name mongodb-btg \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password123 \
  mongo:7
```

---

## Ejecución paso a paso

### 1. Clonar o entrar al directorio del backend

```bash
cd backend
```

### 2. Compilar el proyecto

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn clean compile
```

### 3. Ejecutar el servidor de desarrollo

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn spring-boot:run
```

El servidor inicia en **http://localhost:8081**

Al arrancar, el `DataSeeder` carga automáticamente en MongoDB:
- 5 fondos de inversión (FPV y FIC)
- 1 cliente con balance inicial de **COP $500,000**

### 4. Verificar que el servidor está corriendo

```bash
curl http://localhost:8081/api/v1/client
```

Respuesta esperada:
```json
{
  "id": "1",
  "balance": 500000,
  "notificationPreference": "email",
  "contactInfo": "user@email.com",
  "activeFundIds": []
}
```

---

## Endpoints disponibles

| Método | URL | Descripción |
|--------|-----|-------------|
| `GET` | `/api/v1/client` | Estado del cliente (balance, fondos activos) |
| `GET` | `/api/v1/funds` | Listar fondos con estado de suscripción |
| `POST` | `/api/v1/funds/{id}/subscribe` | Suscribirse a un fondo |
| `DELETE` | `/api/v1/funds/{id}/cancel` | Cancelar suscripción a un fondo |
| `GET` | `/api/v1/transactions` | Historial de transacciones |

### Ejemplos con curl

```bash
# Listar fondos
curl http://localhost:8081/api/v1/funds

# Suscribirse al fondo 1 (FPV_BTG_PACTUAL_RECAUDADORA — $75,000)
curl -X POST http://localhost:8081/api/v1/funds/1/subscribe

# Cancelar suscripción al fondo 1
curl -X DELETE http://localhost:8081/api/v1/funds/1/cancel

# Ver historial de transacciones
curl http://localhost:8081/api/v1/transactions
```

### Reglas de negocio

- Balance inicial: **COP $500,000**
- Si el saldo es insuficiente, la API retorna `HTTP 400`:
  ```json
  { "message": "No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA" }
  ```
- Cancelar un fondo **devuelve** el monto al balance del cliente
- Cada transacción genera un **UUID** único

---

## Documentación Swagger

Con el servidor corriendo, accede a la UI interactiva:

```
http://localhost:8081/swagger-ui.html
```

Especificación OpenAPI en JSON:

```
http://localhost:8081/api-docs
```

---

## Tests

### Ejecutar tests unitarios

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn test
```

### Ejecutar tests + reporte de cobertura

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn verify
```

El reporte HTML de JaCoCo se genera en:

```
target/site/jacoco/index.html
```

### Cobertura mínima requerida: 80%

| Capa | Tests |
|------|-------|
| Domain — `Client` | Todos los métodos (balance, suscripción) |
| Application — Use Cases | Casos feliz + errores de negocio |
| Infrastructure — Repositories | Mapeo document ↔ domain |
| Infrastructure — Notification | Canal email y SMS |
| Presentation — Controllers | HTTP 200 / 400 / 500 |
| Presentation — ExceptionHandler | Excepciones de dominio y genéricas |

Total: **68 tests, 0 failures**

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

## Build para producción

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) mvn clean package -DskipTests
```

El JAR ejecutable se genera en `target/btg-funds-platform-backend-0.0.1-SNAPSHOT.jar`:

```bash
java -jar target/btg-funds-platform-backend-0.0.1-SNAPSHOT.jar \
  --MONGODB_URI="mongodb://admin:password123@localhost:27017/btgfunds_db?authSource=admin"
```
