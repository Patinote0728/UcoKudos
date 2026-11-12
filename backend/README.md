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
- `GET /api/csrf`: obtiene el token CSRF necesario para solicitudes que modifican datos.
- `POST /api/kudos`: publica un Kudo. Requiere autenticación; el autor se obtiene de la cuenta autenticada. El cuerpo requiere `destinatarioId`, `categoriaId` y `mensaje`.
- `GET /api/kudos?pagina=0&tamano=20`: devuelve los Kudos recibidos por la cuenta autenticada, ordenados del más reciente al más antiguo. El tamaño máximo es 50.

Ejemplo de publicación:

```json
{
  "destinatarioId": "00000000-0000-0000-0000-000000000002",
  "categoriaId": "10000000-0000-0000-0000-000000000003",
  "mensaje": "Gracias por tu apoyo al equipo."
}
```

La API valida que el mensaje no esté vacío, que el autor autenticado y el destinatario existan y estén activos, y que la categoría exista y esté activa. Usa `400` para solicitudes inválidas, `401` para falta de autenticación, `403` para solicitudes sin token CSRF válido, `404` para recursos inexistentes, `409` para reglas de negocio y `201` al publicar. El buzón solo muestra Kudos recibidos por la cuenta autenticada.

En el perfil local se crean dos cuentas de demostración: `demo.autor@uco.edu.co` y `demo.destinatario@uco.edu.co`. Ambas usan la contraseña definida por `DEMO_ACCOUNTS_PASSWORD` (por defecto `CambioLocal123!`). Cambia esa clave para tu entorno local. El cargador está limitado al perfil `local`; no se incluye en `prod`.

La autenticación actual usa HTTP Basic y contraseñas BCrypt. Para solicitudes de escritura, solicita el token CSRF en `/api/csrf` y envíalo en la cabecera que indica la respuesta, conservando la cookie `XSRF-TOKEN` de la sesión HTTP. HTTP Basic solo debe usarse en producción detrás de HTTPS; para una experiencia de usuario final conviene evolucionar a inicio de sesión con tokens.

La publicación registra `KUDO_PUBLICADO` en `evento_auditoria` y crea tres trabajos en `cola_notificacion` (correo, WhatsApp y push) dentro de la misma transacción que guarda el Kudo. Si no se puede guardar la auditoría o la cola, también se revierte la publicación.

Un proceso en segundo plano reclama trabajos con `FOR UPDATE SKIP LOCKED`, para evitar que dos instancias envíen el mismo trabajo a la vez. Reintenta los fallos hasta ocho veces con esperas crecientes y recupera trabajos cuyo bloqueo venció. En `local`, el adaptador registra cada canal como `SIMULADA`; no envía mensajes reales. Aún faltan los adaptadores de proveedores para correo, WhatsApp y push, además del almacenamiento físico de adjuntos y la auditoría de intentos fallidos.

## Perfiles

- `local` (predeterminado): conexión local con PostgreSQL.
- `prod`: se añadirá al preparar el despliegue; sus secretos deben venir de variables de entorno.

