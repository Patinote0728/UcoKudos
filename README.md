# UCO Kudos

Plataforma institucional de reconocimientos de la Universidad Católica de Oriente.

## Plan de trabajo

1. **Línea base:** proyecto Spring Boot ejecutable, configuración por entorno y estado de salud.
2. **Dominio y persistencia:** usuarios, roles, programas, categorías y Kudos; PostgreSQL y migraciones.
3. **Publicación de Kudos:** API, validaciones, autorización y auditoría del proceso.
4. **Notificaciones:** correo, WhatsApp y push mediante adaptadores sustituibles; modo local para desarrollo y demostración.
5. **Experiencia de usuario:** interfaz mobile first y consumo seguro de la API.
6. **Calidad y seguridad:** pruebas por capas, análisis estático, escaneo de dependencias e integración continua.
7. **Entrega:** despliegue gratuito si los límites vigentes lo permiten, documentación, evidencias y guía de puesta a punto.

Cada incremento se revisará y explicará antes de avanzar. Los commits y pushes los hará el estudiante.

## Herramientas

- Java 17 o superior y Maven.
- Spring Boot 3.5.7.
- PostgreSQL; para desarrollo local se podrá levantar con Docker Compose.
- GitHub Actions, Sonar y Trivy en sus opciones gratuitas cuando apliquen.

Los proveedores de correo, WhatsApp y hosting tienen límites que pueden cambiar. La solución se diseñará detrás de interfaces para poder usar opciones gratuitas o simuladas en desarrollo sin acoplar el dominio a un proveedor.

## Backend

El backend está en [`backend`](backend). Consulta su README para iniciarlo.

