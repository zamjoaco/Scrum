# Guía de contribución

Gracias por tu interés en contribuir a este proyecto. Esta guía resume el flujo de trabajo y las convenciones que usamos.

## Flujo de ramas (GitFlow)

- `main` y `develop` están **protegidas**: no se permite commitear directamente en ninguna de las dos.
- Todo trabajo nuevo se hace en una rama propia, creada a partir de `develop`, usando el prefijo convencional seguido de un nombre descriptivo:
  - `feature/<nombre-descriptivo>` para nuevas funcionalidades.
  - `fix/<nombre-descriptivo>` para correcciones de bugs.
  - `hotfix/<nombre-descriptivo>` para correcciones urgentes sobre `main`.
  - `release/<version>` para preparar una nueva versión.
- Al terminar el trabajo en una rama, se abre un **Pull Request obligatorio hacia `develop`** (nunca directo a `main`). `main` solo recibe cambios a través de releases.
- Nunca se commitea directo a `main` ni a `develop`: todo cambio pasa por PR y review.

## Conventional Commits

Los mensajes de commit siguen la convención [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<alcance opcional>): <descripción breve>
```

Tipos más usados: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `style`, `ci`.

Ejemplos:

```
feat(backend): agregar endpoint de creación de sprints
fix(frontend): corregir validación del formulario de login
docs: actualizar README con instrucciones de Docker Compose
```

## Pull Requests

Antes de pedir review, el checklist del template de Pull Request debe estar completo. El PR debe:

- Apuntar a `develop` (nunca a `main`).
- Tener un título en formato Conventional Commits.
- Describir brevemente el cambio y su motivación.
- Pasar todas las verificaciones automáticas configuradas en el repositorio.
