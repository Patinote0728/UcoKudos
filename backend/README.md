# UCO Kudos API

## Requisitos

- JDK 17 o superior.
- Maven 3.6.3 o superior.
- PostgreSQL 15 o superior.

## Configuración local

Con Docker Compose, inicia PostgreSQL desde esta carpeta. El contenedor publica el puerto `5433` del equipo para evitar conflictos con instalaciones locales:

```powershell
docker compose up -d database
```

También puedes usar una instalación local de PostgreSQL. La aplicación espera una base `ucokudos` y las credenciales locales `ucokudos` / `ucokudos_local`, o acepta `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` desde el entorno. Las credenciales predeterminadas son únicamente para desarrollo local.

Desde esta carpeta, ejecuta:

```powershell
mvn spring-boot:run
```

La aplicación quedará en `http://localhost:8080`. El estado de salud está disponible en `/actuator/health`. La interfaz web móvil se sirve desde la misma aplicación en `/`, sin un servidor frontend adicional.

## Modelo inicial

Flyway aplica la migración `V1` al iniciar y crea facultades, programas académicos, usuarios, membresías a programas, categorías, Kudos y metadatos de adjuntos. También carga las categorías institucionales iniciales. El modelo impone un único rol por usuario, la activación solo después de aprobación y verificación de correo y celular, y la unicidad del rector y de los coordinadores activos en sus ámbitos.

Las membresías permiten asociar a una persona como estudiante o docente en varios programas. Los archivos adjuntos se guardan fuera de los recursos públicos en `backend/datos/adjuntos` cuando se ejecuta desde esta carpeta. Configura `UCOKUDOS_ADJUNTOS_RUTA` para cambiar la ruta. Esa carpeta está excluida de Git. Cada Kudo admite hasta tres archivos PDF, PNG o JPG de máximo 5 MB cada uno; solo el autor puede cargarlos y solo el autor o destinatario puede consultarlos.

## API

- `GET /api/categorias-kudos`: devuelve las categorías activas.
- `GET /api/programas-academicos`: catálogo público de programas activos, con su facultad.
- `GET /api/usuarios?busqueda=ana`: busca hasta diez personas activas por nombre. Requiere autenticación y devuelve solo identificador, nombre y rol.
- `GET /api/mi-cuenta`: devuelve el nombre y rol de la cuenta autenticada.
- `GET /api/facultades`: catálogo público de facultades activas.
- `POST /api/registros`: crea una cuenta inactiva y su solicitud de aprobación. Acepta nombre, correo, celular internacional, contraseña, rol y el identificador de facultad o programa requerido por ese rol.
- `POST /api/registros/verificar`: verifica correo y celular usando los dos códigos recibidos.
- `GET /api/aprobaciones/pendientes`: lista las solicitudes pendientes para el rol y ámbito de la cuenta autenticada.
- `POST /api/aprobaciones/{id}`: decide una solicitud con `decision` (`APROBADA` o `RECHAZADA`) y comentario opcional.
- `GET /api/csrf`: obtiene el token CSRF necesario para solicitudes que modifican datos.
- `POST /api/kudos`: publica un Kudo. Requiere autenticación; el autor se obtiene de la cuenta autenticada. El cuerpo requiere `destinatarioId`, `categoriaId` y `mensaje`.
- `GET /api/kudos?pagina=0&tamano=20`: devuelve los Kudos recibidos por la cuenta autenticada, ordenados del más reciente al más antiguo. El tamaño máximo es 50.
- `POST /api/kudos/{id}/adjuntos`: recibe un archivo en el campo multipart `archivo`; solo el autor del Kudo puede adjuntarlo.
- `GET /api/kudos/{id}/adjuntos`: lista los metadatos de los adjuntos para el autor o destinatario.
- `GET /api/kudos/{id}/adjuntos/{adjuntoId}/contenido`: descarga el archivo para el autor o destinatario.

La interfaz inicial en `/` permite iniciar sesión, registrar y verificar cuentas, revisar solicitudes para roles aprobadores, buscar destinatarios activos, publicar Kudos con adjuntos y consultar el buzón. Las credenciales se conservan solo en memoria mientras la pestaña está abierta; al recargar se solicita iniciar sesión de nuevo. En perfil local, los códigos para verificar el registro se consultan en el registro de la aplicación.

Ejemplo de publicación:

```json
{
  "destinatarioId": "00000000-0000-0000-0000-000000000002",
  "categoriaId": "10000000-0000-0000-0000-000000000003",
  "mensaje": "Gracias por tu apoyo al equipo."
}
```

La API valida que el mensaje no esté vacío, que el autor autenticado y el destinatario existan y estén activos, y que la categoría exista y esté activa. Usa `400` para solicitudes inválidas, `401` para falta de autenticación, `403` para solicitudes sin token CSRF válido, `404` para recursos inexistentes, `409` para reglas de negocio y `201` al publicar. El buzón solo muestra Kudos recibidos por la cuenta autenticada.

En el perfil local se crean cuentas de demostración para autor, destinatario y la cadena de aprobaciones. Los correos para aprobación son `demo.programa@uco.edu.co`, `demo.facultad@uco.edu.co`, `demo.academico@uco.edu.co`, `demo.rector@uco.edu.co` y `demo.admin@uco.edu.co`. Todas usan la contraseña definida por `DEMO_ACCOUNTS_PASSWORD` (por defecto `CambioLocal123!`). Se crea también una facultad y programa de demostración. Cambia esa clave para tu entorno local. El cargador está limitado al perfil `local`; no se incluye en `prod`.

En local, los códigos de verificación de correo y celular se escriben en el registro de la aplicación; su entrega real por correo y SMS aún debe configurarse antes de desplegar. Los códigos vencen en 10 minutos y permiten hasta cinco intentos. El estudiante y el docente quedan vinculados al programa seleccionado. Para este flujo, ambos requieren aprobación del coordinador de programa; es una regla inicial que deberá confirmarse con la institución.

La autenticación actual usa HTTP Basic y contraseñas BCrypt. Para solicitudes de escritura, solicita el token CSRF en `/api/csrf` y envíalo en la cabecera que indica la respuesta, conservando la cookie `XSRF-TOKEN` de la sesión HTTP. HTTP Basic solo debe usarse en producción detrás de HTTPS; para una experiencia de usuario final conviene evolucionar a inicio de sesión con tokens.

La publicación registra `KUDO_PUBLICADO` en `evento_auditoria` y crea tres trabajos en `cola_notificacion` (correo, WhatsApp y push) dentro de la misma transacción que guarda el Kudo. Si no se puede guardar la auditoría o la cola, también se revierte la publicación.

Un proceso en segundo plano reclama trabajos con `FOR UPDATE SKIP LOCKED`, para evitar que dos instancias envíen el mismo trabajo a la vez. Reintenta los fallos hasta ocho veces con esperas crecientes y recupera trabajos cuyo bloqueo venció. El envío es de tipo *al menos una vez*: si el proveedor acepta un mensaje y el proceso se interrumpe antes de registrar el resultado, podría volver a enviarse en el reintento. `ENVIADA` significa que el proveedor aceptó la solicitud; todavía no se consumen webhooks de entrega final.

En `local`, cada canal queda como `SIMULADA`; no se envían mensajes reales. Para habilitar adaptadores reales, activa `UCOKUDOS_NOTIFICACIONES_REALES_HABILITADAS=true` y configura sus credenciales como variables de entorno. No guardes secretos en los archivos del repositorio.

### Correo por Brevo

Configura `UCOKUDOS_NOTIFICACIONES_BREVO_API_KEY` y `UCOKUDOS_NOTIFICACIONES_BREVO_REMITENTE`. El remitente debe estar registrado/verificado en Brevo. El adaptador usa la API transaccional, no SMTP.

### WhatsApp Cloud API de Meta

Configura `UCOKUDOS_NOTIFICACIONES_WHATSAPP_TOKEN`, `UCOKUDOS_NOTIFICACIONES_WHATSAPP_PHONE_NUMBER_ID`, `UCOKUDOS_NOTIFICACIONES_WHATSAPP_GRAPH_VERSION` y `UCOKUDOS_NOTIFICACIONES_WHATSAPP_TEMPLATE_NAME`; `UCOKUDOS_NOTIFICACIONES_WHATSAPP_TEMPLATE_LANGUAGE` es opcional y usa `es_CO` por defecto. La plantilla aprobada debe tener tres parámetros de cuerpo, en este orden: autor, categoría y mensaje.

La persona también debe autorizar este canal desde `PUT /api/mi-cuenta/preferencias/notificaciones` con `{ "autorizado": true }`; puede revocarlo enviando `false`. Si no hay autorización, el trabajo queda `OMITIDA`. Meta exige una plantilla aprobada para iniciar conversaciones fuera de la ventana de servicio de 24 horas y aplica los precios vigentes a mensajes iniciados por la empresa; revisa su política antes de habilitarlo. [Política de WhatsApp Business](https://business.whatsapp.com/policy/preview?lang=es_LA)

### Push por Firebase Cloud Messaging

Configura `UCOKUDOS_NOTIFICACIONES_FIREBASE_PROJECT_ID` y `GOOGLE_APPLICATION_CREDENTIALS` apuntando a una cuenta de servicio con permiso FCM. El cliente registra el FID autenticado con `POST /api/dispositivos-push` y puede revocarlo con `DELETE /api/dispositivos-push/{id}`. El backend nunca devuelve el FID. Sin dispositivos registrados, el trabajo queda `OMITIDA`. FCM no tiene costo de uso según Firebase, aunque el proyecto Firebase/Google Cloud puede tener otros servicios facturables. [Firebase Cloud Messaging](https://firebase.google.com/products/cloud-messaging)

El estado `ENVIADA` confirma aceptación por el proveedor, no entrega al teléfono o bandeja. Los canales reales requieren credenciales válidas y no han sido ejercitados contra cuentas externas. La auditoría de intentos fallidos de notificación sigue pendiente.

## Verificación automática

El flujo `.github/workflows/verificacion.yml` compila el proyecto con Java 17 en cada push y pull request. También analiza dependencias, secretos y configuración con Trivy. El análisis reporta hallazgos sin bloquear todavía la compilación; revisa los resultados y endurece el umbral cuando se haya establecido una línea base.

Las pruebas automatizadas se ejecutan con `mvn -B test` y requieren PostgreSQL disponible en el puerto `5433` (por ejemplo, con `docker compose up -d database`). La prueba de integración crea un esquema aleatorio aislado, recorre el registro, la verificación, la aprobación, la publicación, la auditoría y la cola de notificaciones, y elimina ese esquema al terminar; también guarda y descarga un adjunto. La suite unitaria cubre la autorización del autor, los tipos de archivo permitidos y el máximo de tres adjuntos por Kudo. El flujo de GitHub inicia PostgreSQL como servicio e incluye ambas pruebas mediante `mvn verify`.

## Perfiles

- `local` (predeterminado): conexión local con PostgreSQL.
- `prod`: se añadirá al preparar el despliegue; sus secretos deben venir de variables de entorno.

