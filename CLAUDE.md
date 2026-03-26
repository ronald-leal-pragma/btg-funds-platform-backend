# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Idioma
- Responde siempre en **español**
- Los términos técnicos pueden quedar en inglés (ej: endpoint, commit, deploy)

## Project Overview

BTG Pactual Fullstack Technical Assessment — plataforma de gestión de fondos (FPV/FIC) que permite a un cliente suscribirse a fondos de inversión, cancelar suscripciones, ver historial de transacciones y recibir notificaciones por email/SMS.

**Tech stack**: React (frontend) + Java/Spring Boot (backend) + MongoDB + AWS CloudFormation.

## Arquitectura Backend — Clean Architecture + DDD

Aplicar estrictamente **Clean Architecture** con las siguientes capas:
```
backend/
├── domain/                  # Núcleo — sin dependencias externas
│   ├── model/               # Entidades y Value Objects (Fund, Client, Transaction)
│   ├── repository/          # Interfaces de repositorio (ports)
│   └── service/             # Lógica de negocio pura (domain services)
│
├── application/             # Casos de uso (orquesta el dominio)
│   ├── usecase/             # SubscribeFundUseCase, CancelFundUseCase, etc.
│   ├── dto/                 # Request/Response DTOs
│   └── port/                # Interfaces hacia afuera (notificaciones, etc.)
│
├── infrastructure/          # Implementaciones concretas (adapters)
│   ├── persistence/         # Repositorios MongoDB (Spring Data)
│   ├── notification/        # Implementación Email/SMS (AWS SES/SNS)
│   ├── config/              # Beans de Spring, configuración
│   └── exception/           # Manejo global de errores
│
└── presentation/            # Capa HTTP (controllers REST)
    ├── controller/
    └── advice/              # @ControllerAdvice para excepciones
```

## Stack Tecnológico

### Backend
- **Java 21** (usar Records, Sealed classes, Pattern matching)
- **Spring Boot 3.x**
- **Spring Data MongoDB**
- **Maven** como build tool
- **MapStruct** para mapeo de DTOs
- **Lombok** para reducir boilerplate
- **JUnit 5 + Mockito** para tests
- **OpenAPI/Swagger** para documentar endpoints

### Frontend
- **React 18** + **TypeScript**
- **Vite** como bundler
- **TailwindCSS** para estilos
- **React Query** para manejo de estado servidor
- **Axios** para HTTP

### Base de datos
- **MongoDB** (local con Docker para desarrollo)

### AWS / Deployment
- **AWS CloudFormation**
- Backend: **Lambda con Spring Boot** o **ECS Fargate**
- Frontend: **S3 + CloudFront**
- Notificaciones: **SES** (email) + **SNS** (SMS)

## Buenas Prácticas — OBLIGATORIAS

### Código
- Aplicar principios **SOLID** en todo momento
- Usar **inmutabilidad** donde sea posible (Records de Java 21)
- **Nunca** exponer entidades de dominio directamente en la API — siempre usar DTOs
- Manejo de errores con **excepciones de dominio** personalizadas
- Validaciones con **Bean Validation** (@Valid, @NotNull, etc.)
- Logs con **SLF4J + Logback**

### Tests
- Cobertura mínima del **80%**
- Tests unitarios para casos de uso y servicios de dominio
- Tests de integración para repositorios
- Nombrar tests: `should_[resultado]_when_[condición]`

### Git
- Commits en inglés siguiendo **Conventional Commits**
  - `feat:`, `fix:`, `test:`, `refactor:`, `docs:`
- Una rama por feature

## Business Rules

- Cliente único, sin autenticación.
- Balance inicial: **COP $500,000**
- Cada transacción genera un **UUID** único
- Si saldo insuficiente: `"No tiene saldo disponible para vincularse al fondo <Nombre>"`
- Cancelar un fondo **devuelve** el monto al balance
- Al suscribirse: notificar por **email O SMS** según preferencia del cliente

## Fondos (datos semilla)

| Id | Nombre | Monto Mínimo | Categoría |
|----|--------|-------------|-----------|
| 1 | FPV_BTG_PACTUAL_RECAUDADORA | COP $75,000 | FPV |
| 2 | FPV_BTG_PACTUAL_ECOPETROL | COP $125,000 | FPV |
| 3 | DEUDAPRIVADA | COP $50,000 | FIC |
| 4 | FDO-ACCIONES | COP $250,000 | FIC |
| 5 | FPV_BTG_PACTUAL_DINAMICA | COP $100,000 | FPV |

## API Endpoints

- `GET  /api/v1/funds` — listar fondos con estado de suscripción
- `POST /api/v1/funds/{id}/subscribe` — suscribirse a un fondo
- `DELETE /api/v1/funds/{id}/cancel` — cancelar suscripción
- `GET  /api/v1/transactions` — historial de transacciones
- `GET  /api/v1/client` — estado del cliente (balance, fondos activos, preferencia)

## Comandos

### Backend
```bash
cd backend
mvn spring-boot:run          # servidor dev en :8080
mvn test                     # ejecutar todos los tests
mvn verify                   # tests + análisis de cobertura
mvn clean package            # build para producción
```

### Frontend
```bash
cd frontend
npm install
npm run dev      # servidor dev
npm test         # unit tests
npm run build    # build producción
```

## Modelo de Datos MongoDB

### Colección: `client`
```json
{ "id": "1", "balance": 500000, "notification_preference": "email", "contact_info": "user@email.com" }
```

### Colección: `funds`
```json
{ "id": "1", "name": "FPV_BTG_PACTUAL_RECAUDADORA", "min_amount": 75000, "category": "FPV" }
```

### Colección: `transactions`
```json
{ "id": "UUID", "type": "apertura|cancelacion", "fund_id": "1", "fund_name": "...", "amount": 75000, "timestamp": "ISO8601" }
```

## Parte 2 - Consultas SQL (20%)

Además del desarrollo Fullstack, el proyecto requiere resolver un ejercicio de SQL puro basado en una base de datos relacional llamada "BTG" que cuenta con las siguientes tablas:

### Esquema Relacional (Base de datos "BTG")
- **Cliente** (id PK, nombre, apellidos, ciudad)
- **Sucursal** (id PK, nombre, ciudad)
- **Producto** (id PK, nombre, tipoProducto)
- **Inscripción** (idProducto PK/FK, idCliente PK/FK)
- **Disponibilidad** (idSucursal PK/FK, idProducto PK/FK) - *Nota: no todas las sucursales ofrecen los mismos productos.*
- **Visitan** (idSucursal PK/FK, idCliente PK/FK, fechaVisita)

### Requerimiento SQL
Debes crear un archivo llamado `resolucion_parte2.sql` en la raíz del proyecto (o en la carpeta `database/` si existe) que contenga la consulta SQL para resolver el siguiente problema:

> **"Obtener los nombres de los clientes los cuales tienen inscrito algún producto disponible sólo en las sucursales que visitan."**

### Reglas para el SQL
- Usa sintaxis SQL estándar (ANSI SQL) o especifica si estás usando el dialecto de PostgreSQL/MySQL.
- Asegúrate de usar los `JOIN` correctos respetando las llaves foráneas indicadas en el esquema.
- El código SQL debe estar formateado, limpio y comentado explicando la lógica de la consulta.

## Autonomía y permisos

### Hacer SIN preguntar
- Leer cualquier archivo del proyecto
- Crear archivos nuevos en src/
- Escribir y correr tests
- Instalar dependencias listadas en el proyecto

### Preguntar SIEMPRE antes de
- Eliminar o renombrar archivos
- Modificar archivos de configuración (pom.xml, application.yml)
- Hacer cambios en más de 3 archivos a la vez
- Tocar cualquier cosa relacionada con AWS/deployment
- Cambiar la arquitectura o estructura de carpetas

### NUNCA hacer
- Push a git
- Modificar archivos fuera del proyecto
- Ejecutar comandos de base de datos en producción