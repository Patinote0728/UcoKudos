# UCO Kudos API

## Requisitos

- JDK 17 o superior.
- Maven 3.6.3 o superior.
- PostgreSQL 15 o superior.

## Configuración local

Con Docker Compose, inicia PostgreSQL desde esta carpeta:

```powershell
docker compose up -d database
```

También puedes usar una instalación local de PostgreSQL. La aplicación espera una base `ucokudos` y las credenciales locales `ucokudos` / `ucokudos_local`, o acepta `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` desde el entorno. Las credenciales predeterminadas son únicamente para desarrollo local.

Desde esta carpeta, ejecuta:

```powershell
mvn spring-boot:run
```

La aplicación quedará en `http://localhost:8080`. El estado de salud está disponible en `/actuator/health`.

## Perfiles

- `local` (predeterminado): conexión local con PostgreSQL.
- `prod`: se añadirá al preparar el despliegue; sus secretos deben venir de variables de entorno.

