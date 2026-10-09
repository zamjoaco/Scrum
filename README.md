# Scrum

Aplicación de gestión de proyectos con metodología Scrum (tableros, sprints, historias de usuario y tareas), desarrollada como proyecto de portfolio profesional full-stack.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)
![Backend](https://img.shields.io/badge/backend-Spring%20Boot-6DB33F)
![Frontend](https://img.shields.io/badge/frontend-Angular-DD0031)
![Database](https://img.shields.io/badge/database-PostgreSQL%20%2B%20MongoDB-336791)

## Arquitectura

El backend está construido en **Spring Boot (Java)** siguiendo los principios de **Clean Architecture**, separando el dominio de negocio de los detalles de infraestructura (persistencia, web, mensajería) para mantener el núcleo de la aplicación independiente de frameworks.

El frontend está construido en **Angular**, modularizado en tres capas:

- **Core**: servicios singleton, interceptores y lógica transversal a toda la aplicación.
- **Shared**: componentes, directivas y pipes reutilizables sin lógica de negocio propia.
- **Features**: módulos de funcionalidad (tableros, sprints, historias, etc.), cada uno con su propio dominio.

El contrato de la API REST está definido en [`docs/api/openapi.yaml`](docs/api/openapi.yaml) siguiendo la especificación OpenAPI, y es la fuente de verdad para la comunicación entre frontend y backend.

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot (Java) |
| Frontend | Angular |
| Base de datos relacional | PostgreSQL |
| Base de datos NoSQL | MongoDB (auditoría, comentarios, notificaciones) |
| Cache / Rate limiting | Redis |
| Contenedores | Docker / Docker Compose |

## Cómo levantar el entorno local

El entorno de desarrollo local se levanta con Docker Compose desde la carpeta [`infra/`](infra/). Consultá el [README de `infra/`](infra/README.md) para el detalle de los servicios, variables de entorno y comandos.

```bash
cd infra
docker compose up -d
```

## Metodología de desarrollo

Este repositorio sigue **GitFlow** (ramas `main` y `develop` protegidas, ramas de trabajo con prefijos convencionales como `feature/`) y la convención **Conventional Commits** para los mensajes de commit. El detalle completo del flujo de trabajo, convenciones y checklist de Pull Request está documentado en [`CONTRIBUTING.md`](CONTRIBUTING.md).

## Licencia

Este proyecto está licenciado bajo los términos de la licencia [MIT](LICENSE).
