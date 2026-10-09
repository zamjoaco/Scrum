# Scrum App API — OpenAPI spec

Spec OpenAPI 3.0.3 dividida en multiples archivos (no un unico YAML gigante).
Esto permite que varios workers/devs trabajen en paralelo sobre distintos
recursos sin pisarse.

## Estructura de carpetas

```
docs/api/
├── openapi.yaml                       # documento raiz: info, servers, paths, components (via $ref)
├── README.md                          # este archivo
├── paths/                             # un archivo por recurso, cada uno con sus Path Item Objects
│   ├── auth.yaml
│   ├── workspaces.yaml
│   ├── projects.yaml
│   ├── sprints.yaml
│   ├── board.yaml
│   ├── stories.yaml
│   ├── tasks.yaml
│   ├── comments.yaml
│   ├── activity.yaml
│   └── notifications.yaml
└── components/
    ├── securitySchemes.yaml           # bearerAuth (JWT)
    ├── schemas/
    │   ├── common.yaml                # ProblemDetail, PageMetadata, patron de paginado
    │   └── <recurso>.yaml             # un archivo por recurso (lo crea el worker de ese recurso)
    └── responses/
        └── errors.yaml                # BadRequest, Unauthorized, Forbidden, NotFound, Conflict
```

## Convencion de nombres de archivo

- `paths/<recurso-en-plural-kebab-o-simple>.yaml` — un archivo por dominio de
  recurso, ya listado arriba. No crear archivos `paths/` adicionales sin
  coordinar: la lista de 10 archivos es fija.
- `components/schemas/<recurso>.yaml` — un archivo por recurso para sus
  schemas propios (ej `components/schemas/project.yaml` define `Project`,
  `ProjectPage`, `ProjectCreateRequest`, etc). `common.yaml` es solo para
  schemas transversales (errores, paginado), no para schemas de dominio.
- Nombres de schema: **PascalCase** (`UserStory`, `ProjectPage`,
  `SprintCreateRequest`).
- Nombres de campo en el wire (JSON): **snake_case** (`first_name`,
  `created_at`, `sprint_id`). Esto aplica a requests, responses y query
  params — es la convencion de todo el API, sin excepciones por recurso.

## Como se referencian schemas entre archivos

OpenAPI 3.0 soporta `$ref` entre archivos usando JSON Pointer relativo al
archivo que contiene la referencia (no al root). Reglas:

- Desde `paths/<recurso>.yaml` hacia un schema propio:
  `$ref: '../components/schemas/<recurso>.yaml#/<Schema>'`
- Desde `paths/<recurso>.yaml` hacia un schema de otro recurso (ej una
  story que referencia su proyecto):
  `$ref: '../components/schemas/project.yaml#/Project'`
- Desde `paths/<recurso>.yaml` hacia los errores comunes:
  `$ref: '../components/responses/errors.yaml#/NotFound'`
- Desde `components/schemas/<recurso>.yaml` hacia `common.yaml`:
  `$ref: './common.yaml#/PageMetadata'`
- Desde `openapi.yaml` (raiz) hacia cualquier archivo: ruta relativa
  empezando con `./` (ej `./components/securitySchemes.yaml#/bearerAuth`).

Nunca referenciar con ruta absoluta de filesystem. Siempre relativa al
archivo que escribe el `$ref`.

## Patron de paginado

Todo endpoint de listado paginado devuelve un envelope con esta forma:

```yaml
<Recurso>Page:
  type: object
  properties:
    items:
      type: array
      items:
        $ref: '../components/schemas/<recurso>.yaml#/<Recurso>'
    page:
      $ref: '../components/schemas/common.yaml#/PageMetadata'
  required: [items, page]
```

Ejemplo: `ProjectPage` tiene `items: Project[]` + `page: PageMetadata`. Ver
el comentario en `components/schemas/common.yaml` para el detalle completo.
No reinventar paginado por recurso — todos reusan `PageMetadata`.

## Errores

Todas las respuestas de error usan `application/problem+json` con el
schema `ProblemDetail` (RFC 7807), definido en
`components/schemas/common.yaml`. Las respuestas reutilizables
(`BadRequest`, `Unauthorized`, `Forbidden`, `NotFound`, `Conflict`) viven en
`components/responses/errors.yaml` y se enlazan desde cada endpoint con:

```yaml
responses:
  '404':
    $ref: '../components/responses/errors.yaml#/NotFound'
```

## Seguridad

`bearerAuth` (JWT, header `Authorization: Bearer <token>`) esta definido en
`components/securitySchemes.yaml` y aplicado globalmente en `openapi.yaml`.
Un endpoint publico (ej login) debe sobreescribir con `security: []` en su
Operation Object.
