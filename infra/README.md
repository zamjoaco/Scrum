# Infraestructura local (Docker Compose)

Este `docker-compose.yml` levanta los tres servicios de persistencia y cache que usa la app
(backend Spring Boot + frontend Angular):

- **postgres** (Postgres 16): base de datos relacional principal. Expuesto en `localhost:5432`.
- **mongo** (MongoDB 7): base de datos NoSQL. Expuesto en `localhost:27017`.
- **redis** (Redis 7 alpine): cache y rate-limiting, protegido con password. Expuesto en `localhost:6379`.

Los tres servicios comparten la red `scrum-network` y persisten datos en volumenes nombrados
(`postgres-data`, `mongo-data`, `redis-data`).

## Como levantarlo

1. Copiar el archivo de variables de entorno de ejemplo:

   ```sh
   cp .env.example .env
   ```

   Editar `.env` y reemplazar los valores de ejemplo (`changeme-in-prod`) por valores propios.

2. Levantar los servicios desde la carpeta `infra`:

   ```sh
   docker compose up -d
   ```

3. Para bajarlos:

   ```sh
   docker compose down
   ```

   Agregar `-v` si tambien se quieren borrar los volumenes de datos.

## Como verificar que los healthchecks estan OK

Con los contenedores corriendo, revisar el estado de salud de cada servicio:

```sh
docker compose ps
```

La columna `STATUS` debe mostrar `healthy` para `postgres`, `mongo` y `redis`. Tambien se puede
inspeccionar un servicio puntual:

```sh
docker inspect --format='{{.State.Health.Status}}' scrum-postgres
docker inspect --format='{{.State.Health.Status}}' scrum-mongo
docker inspect --format='{{.State.Health.Status}}' scrum-redis
```

Cada comando debe devolver `healthy` una vez que el servicio terminó de inicializar.
