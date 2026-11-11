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

## Modelo inicial

Flyway aplica la migración `V1` al iniciar y crea facultades, programas académicos, usuarios, membresías a programas, categorías, Kudos y metadatos de adjuntos. También carga las categorías institucionales iniciales. El modelo impone un único rol por usuario, la activación solo después de aprobación y verificación de correo y celular, y la unicidad del rector y de los coordinadores activos en sus ámbitos.

Las membresías permiten asociar a una persona como estudiante o docente en varios programas. Los adjuntos guardan metadatos y una clave de almacenamiento; el almacenamiento físico y sus controles de acceso se añadirán junto al flujo de publicación.

## API inicial

- `GET /api/categorias-kudos`: devuelve las categorías activas.
- `POST /api/kudos`: publica un Kudo. El cuerpo requiere `autorId`, `destinatarioId`, `categoriaId` y `mensaje`.
- `GET /api/kudos?destinatarioId=<UUID>&pagina=0&tamano=20`: devuelve los Kudos recibidos, ordenados del más reciente al más antiguo. El tamaño máximo es 50.

Ejemplo de publicación:

```json
{
  "autorId": "00000000-0000-0000-0000-000000000001",
  "destinatarioId": "00000000-0000-0000-0000-000000000002",
  "categoriaId": "10000000-0000-0000-0000-000000000003",
  "mensaje": "Gracias por tu apoyo al equipo."
}
```

La API valida que el mensaje no esté vacío, que autor y destinatario existan y estén activos, y que la categoría exista y esté activa. Usa `400` para solicitudes inválidas, `404` para recursos inexistentes, `409` para reglas de negocio y `201` al publicar.

**Límite actual:** todavía no se implementó autenticación. `autorId` se recibe en el cuerpo y, por lo tanto, puede falsificarse. Estos endpoints sirven para desarrollo local; no se deben publicar hasta derivar el autor de una identidad autenticada y proteger las consultas.

## Perfiles

- `local` (predeterminado): conexión local con PostgreSQL.
- `prod`: se añadirá al preparar el despliegue; sus secretos deben venir de variables de entorno.

