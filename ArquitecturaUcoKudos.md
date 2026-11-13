# Diseño de arquitectura — UCO Kudos

## C4 nivel 2 — contenedores

```mermaid
C4Container
    title UCO Kudos — C4 nivel 2

    Person(usuario, "Usuario institucional", "Estudiante, docente, coordinador, rector o administrador")

    System_Boundary(kudos, "UCO Kudos") {
        Container(web, "Aplicación web", "HTML, CSS, JavaScript", "Interfaz responsive; servida como estáticos por la API")
        Container(api, "Backend modular", "Java, Spring Boot", "API REST, seguridad, reglas de negocio y procesador programado")
        ContainerDb(db, "Base de datos", "PostgreSQL + Flyway", "Datos de negocio y cola de notificaciones")
        Container(storage, "Almacenamiento de adjuntos", "Supabase / filesystem local", "Binarios fuera de la transacción")
    }
    System_Ext(mail, "Correo", "Brevo")
    System_Ext(whatsapp, "WhatsApp Business", "Meta Cloud API")
    System_Ext(push, "Push", "Firebase Cloud Messaging")

    Rel(usuario, web, "1. Usa", "HTTPS")
    Rel(web, api, "2. Solicita", "HTTPS / JSON")
    Rel(api, db, "3. Lee/escribe", "JDBC / JPA")
    Rel(api, storage, "4. Carga/consulta", "API o filesystem")
    Rel(api, mail, "5. Envía (cola)", "HTTPS API")
    Rel(api, whatsapp, "6. Envía (cola)", "HTTPS API")
    Rel(api, push, "7. Envía push", "Firebase Admin SDK")
```

## Diagrama de componentes — backend

```mermaid
%%{init: {"theme":"dark","themeVariables":{"darkMode":true,"background":"#111111","primaryColor":"#3A2B40","primaryTextColor":"#FFFFFF","primaryBorderColor":"#A17EAA","lineColor":"#D0D0D0","secondaryColor":"#1D3020","tertiaryColor":"#1B2B42","clusterBkg":"#111111","clusterBorder":"#BDBDBD","fontFamily":"Arial"}}}%%
flowchart LR
    Cliente[Cliente web<br/>Navegador]
    subgraph Aplicacion[Aplicación Spring Boot — monolito modular]
      Plataforma[Java / JVM]
      Framework[Spring Boot<br/>Web, Security, Validation]
      Seg[Seguridad<br/>filtros y autorización]
      API[API REST<br/>controladores y validación]
      U[Usuarios<br/>registro, aprobaciones y cuenta]
      R[Reconocimiento<br/>Kudos, categorías y adjuntos]
      N[Notificaciones<br/>preferencias y cola]
      A[Auditoría<br/>eventos de negocio]
      Acad[Gestión académica<br/>facultades y programas]
      Persist[Persistencia JPA<br/>repositorios]
      Proc[Procesador programado<br/>lotes y reintentos]
      Files[Almacenamiento de adjuntos<br/>puerto y adaptadores]
      Canales[Adaptadores de notificación<br/>correo, WhatsApp y push]
    end
    DB[(PostgreSQL)]
    Obj[(Supabase Storage / local)]
    Ext[Proveedores externos<br/>Brevo, Meta, Firebase]
    Cliente --> Seg --> API
    Plataforma -. ejecuta .-> Framework
    Framework -. integra .-> API
    API --> U
    API --> R
    API --> N
    API --> Acad
    R --> A
    R --> N
    U --> Persist
    R --> Persist
    N --> Persist
    A --> Persist
    Acad --> Persist
    Persist --> DB
    Proc --> N
    Proc --> Canales --> Ext
    R --> Files --> Obj
    classDef propio fill:#3A2B40,stroke:#A17EAA,color:#FFFFFF,stroke-width:1px;
    classDef framework fill:#1D3020,stroke:#668A53,color:#FFFFFF,stroke-width:1px;
    classDef plataforma fill:#1B2B42,stroke:#6B8FB8,color:#FFFFFF,stroke-width:1px;
    classDef runtime fill:#1D3020,stroke:#668A53,color:#FFFFFF,stroke-width:1px;
    classDef externo fill:#332700,stroke:#B78A00,color:#FFFFFF,stroke-width:1px;
    class Cliente,Plataforma,DB,Obj plataforma;
    class Seg,API,U,R,N,A,Acad,Persist,Proc,Files,Canales propio;
    class Framework runtime;
    class Ext externo;
```

**Leyenda:** morado = componentes propios; verde = framework; azul = plataforma y almacenamiento base; ocre = servicios externos.

**Responsabilidades y límites:** `api` traduce HTTP a llamadas de aplicación y no contiene reglas de negocio. Los servicios de aplicación coordinan casos de uso; dominio contiene entidades y estados. Repositorios y puertos de almacenamiento aíslan infraestructura. En la implementación actual algunos servicios de aplicación dependen directamente de interfaces concretas de repositorio Spring; es una modularidad por paquetes/capas, no una Clean Architecture estricta en todos los puntos.

## Diagrama de paquetes

```mermaid
%%{init: {"theme":"dark","themeVariables":{"darkMode":true,"background":"#111111","primaryColor":"#111111","primaryTextColor":"#FFFFFF","primaryBorderColor":"#E6E6E6","lineColor":"#E6E6E6","secondaryColor":"#111111","tertiaryColor":"#111111","clusterBkg":"#111111","clusterBorder":"#E6E6E6","fontFamily":"Arial"}}}%%
flowchart TB
  subgraph root[co.edu.uco.kudos — backend Java]
    direction TB
    subgraph seguridad[seguridad]
      SegConfig[configuración]
      SegApi[api]
    end
    subgraph usuario[usuario]
      UApi[api]
      UApp[aplicacion]
      UDom[dominio]
      UPers[persistencia]
      UInfra[infraestructura]
    end
    subgraph reconocimiento[reconocimiento]
      RApi[api]
      RApp[aplicacion]
      RDom[dominio]
      RPers[persistencia]
      RInfra[infraestructura]
    end
    subgraph notificacion[notificacion]
      NApi[api]
      NApp[aplicacion]
      NDom[dominio]
      NPers[persistencia]
      NInfra[infraestructura]
    end
    subgraph academico[academico]
      AcApi[api]
      AcDom[dominio]
      AcPers[persistencia]
    end
    subgraph auditoria[auditoria]
      AudDom[dominio]
      AudPers[persistencia]
    end
    subgraph comun[comun]
      ComApi[api]
      ComError[error]
    end
  end
  UApi --> UApp --> UDom
  UApp --> UPers
  UApp --> UInfra
  RApi --> RApp --> RDom
  RApp --> RPers
  RApp --> RInfra
  NApi --> NApp --> NDom
  NApp --> NPers
  NApp --> NInfra
  AcApi --> AcPers
  SegConfig --> UApp
  RApp --> UDom
  RApp --> NApp
  RApp --> AudDom
  UApp --> AudDom
  AcPers --> UApp
  RApp --> ComError
  UApp --> ComError
  NApp --> ComError
  classDef paquete fill:#111111,stroke:#E6E6E6,color:#FFFFFF,stroke-width:1px;
  classDef interno fill:#111111,stroke:#BDBDBD,color:#FFFFFF,stroke-width:1px;
  class SegConfig,SegApi,UApi,UApp,UDom,UPers,UInfra,RApi,RApp,RDom,RPers,RInfra,NApi,NApp,NDom,NPers,NInfra,AcApi,AcDom,AcPers,AudDom,AudPers,ComApi,ComError interno;
```

El contorno y las pestañas de paquetes replican la vista UML de la referencia; las flechas indican dependencias observadas, no flujo de ejecución. El frontend no aparece como paquete Java porque en UCO Kudos son recursos estáticos servidos por el backend.

## Diagramas de secuencia principales

### Publicar Kudo y programar notificaciones

```mermaid
%%{init: {"theme":"dark","themeVariables":{"darkMode":true,"background":"#111111","primaryColor":"#1B2B42","primaryTextColor":"#FFFFFF","primaryBorderColor":"#2763B5","lineColor":"#A8A8A8","actorBkg":"#1B2B42","actorBorder":"#2763B5","actorTextColor":"#FFFFFF","signalColor":"#D0D0D0","signalTextColor":"#FFFFFF","labelBoxBkgColor":"#1B2B42","labelBoxBorderColor":"#2763B5","labelTextColor":"#FFFFFF","noteBkgColor":"#2A2410","noteBorderColor":"#B78A00","noteTextColor":"#FFFFFF","fontFamily":"Arial"}}}%%
sequenceDiagram
    actor Autor
    participant Web as Aplicación web
    participant Sec as Spring Security
    participant Ctrl as KudoControlador
    participant Srv as ServicioKudos
    participant DB as PostgreSQL
    participant Cola as ServicioColaNotificaciones
    Autor->>Web: Completa destinatario, categoría y mensaje
    Web->>Sec: POST /api/kudos (HTTP Basic + CSRF)
    Sec->>Ctrl: Solicitud autenticada
    Ctrl->>Srv: publicar(solicitud, correo autenticado)
    Srv->>DB: Buscar autor activo, destinatario activo y categoría activa
    Srv->>DB: Guardar Kudo
    Srv->>DB: Guardar evento KUDO_PUBLICADO
    Srv->>Cola: programar(Kudo)
    Cola->>DB: Insertar trabajos por canal habilitado
    Srv-->>Ctrl: KudoRespuesta
    Ctrl-->>Web: 201 Created
    Web-->>Autor: Confirmación
    Note over Srv,DB: Las escrituras participan en la transacción de aplicación.
```



## Drivers arquitectónicos

| ID | Driver y prioridad | Respuesta arquitectónica de la línea base | Evidencia / brecha |
|---|---|---|---|
| DRV-001 | Movilidad; 90% móvil, alta | Interfaz web estática separada de la API lógica y diseño responsive; servir recursos desde el backend reduce despliegue inicial. | Revisar estilos y probar tamaños móviles; el documento define Mobile First, pero no aporta métricas de usabilidad/performance móvil. |
| DRV-002 | Alta demanda de lectura, alta | Consultas paginadas; límite de 50 registros por página; transacciones de solo lectura en listado. | JPA/PostgreSQL; índices de consultas críticas deben comprobarse con volumen real. Caché no identificada en la línea base. |
| DRV-003 | Multicanal, alta | Puerto `AdaptadorNotificaciones`, adaptadores por proveedor y cola persistida con reintentos. | Mail, WhatsApp, push disponibles como adaptadores. Operación efectiva depende de credenciales, consentimiento y perfil; no hay broker externo. |
| DRV-004 | Adjuntos, alta | Puerto `AlmacenamientoAdjuntos` y adaptadores Supabase/local; metadatos separados del binario. | `ServicioAdjuntosKudo`; políticas de tamaño/tipo, análisis antimalware, respaldo y retención deben definirse/desplegarse. |
| DRV-005 | Seguridad, crítica | Spring Security, autenticación HTTP Basic sin estado, autorización autenticada, validación de entrada y CSRF. | Revisar autorización por rol en cada caso de uso, transporte HTTPS y secretos externos; no guardarlos en el repositorio. |
| DRV-006 | Observabilidad y trazabilidad, alta | Actuator expone health/info; auditoría de eventos en base; logs del procesador capturan fallos sin el contenido sensible. | Métricas y trazas distribuidas no están expuestas/configuradas en `application.yml`; completar antes de afirmar observabilidad integral. |
| DRV-007 | Automatización DevSecOps, alta | Contenedorización y configuración de despliegue (`Dockerfile`, `render.yaml`); pruebas existentes. | En los archivos listados no aparecen workflows CI/CD ni configuración Sonar/Trivy; agregar pipeline y evidencias para cumplir el enunciado. |

## ADR — decisiones arquitectónicas

Estado: decisiones documentadas retrospectivamente a partir de la implementación actual. Si se cambia la solución, registrar una nueva ADR en vez de sobrescribir la historia.

### ADR-001 — Monolito modular como línea base

- **Estado:** Aceptada.
- **Contexto:** El alcance incluye varios módulos de negocio, pero el equipo y la línea base son individuales y el despliegue debe ser sencillo.
- **Decisión:** Desplegar una aplicación Spring Boot única organizada en módulos por dominio (`usuario`, `reconocimiento`, `notificacion`, `academico`, `auditoria`).
- **Consecuencias:** Menor costo operacional, transacciones locales y despliegue simple. El escalado es principalmente vertical/replicando toda la aplicación; los módulos comparten proceso y base de datos. Extraer servicios requiere necesidad y límites medidos.

### ADR-002 — PostgreSQL como almacenamiento transaccional

- **Estado:** Aceptada.
- **Contexto:** Identidades, relaciones académicas, publicaciones, auditoría y trabajos de notificación requieren integridad referencial y consistencia.
- **Decisión:** Usar PostgreSQL mediante Spring Data JPA; evolucionar esquema con Flyway y validar el mapeo Hibernate (`ddl-auto: validate`).
- **Consecuencias:** Buen soporte para consultas relacionales y transacciones; el escalado de lectura futuro puede incorporar índices, réplicas o caché tras medir. La cola comparte disponibilidad y capacidad con la base operacional.

### ADR-003 — Cola de notificaciones persistida en PostgreSQL

- **Estado:** Aceptada.
- **Contexto:** La publicación del Kudo no debería esperar las llamadas a proveedores y los fallos externos requieren reintentos.
- **Decisión:** Encolar un trabajo por canal en la misma transacción lógica del caso de uso y procesarlo en lotes con bloqueo temporal, espera incremental y máximo de ocho intentos.
- **Consecuencias:** No requiere broker adicional y conserva trabajos ante reinicios; genera carga en la base y el patrón puede duplicar entregas si un proveedor acepta el mensaje y falla la confirmación local. Las integraciones deben usar idempotencia cuando esté disponible. Evolucionar a outbox/broker si escala o aislamiento operacional lo exige.

### ADR-004 — Almacenamiento de binarios detrás de un puerto

- **Estado:** Aceptada.
- **Contexto:** Los adjuntos requieren poder cambiar entre desarrollo local y almacenamiento administrado.
- **Decisión:** Encapsular operaciones en `AlmacenamientoAdjuntos` con adaptadores local y Supabase.
- **Consecuencias:** Facilita cambiar el proveedor; requiere política de acceso, expiración/enlaces seguros, límites, respaldo y reconciliación de metadatos.

### ADR-005 — Adaptadores por canal de notificación

- **Estado:** Aceptada.
- **Contexto:** Correo, WhatsApp y push tienen APIs, credenciales, errores y requisitos de consentimiento distintos.
- **Decisión:** Seleccionar un `AdaptadorNotificaciones` por canal, configurando implementaciones productivas y de demostración/deshabilitadas.
- **Consecuencias:** Aísla dependencias externas y permite sustituir proveedores. El estado de demo/simulado no equivale a entrega real; WhatsApp debe respetar autorización y reglas de plantillas del proveedor.

### ADR-006 — Autenticación de aplicación web con Spring Security

- **Estado:** Aceptada.
- **Contexto:** Las operaciones requieren usuario autenticado; el cliente web se sirve desde el mismo origen.
- **Decisión:** Centralizar autenticación en Spring Security con HTTP Basic y política sin estado; proteger solicitudes mutables con CSRF y exponer identidad a través del principal autenticado.
- **Consecuencias:** Reduce lógica de autenticación dispersa y evita estado de sesión en servidor. HTTP Basic exige HTTPS; deben revisarse autorización por rol y caso de uso, manejo de credenciales, protección contra fuerza bruta y configuración CSRF en producción.

### ADR-007 — Auditoría de eventos de negocio en base de datos

- **Estado:** Aceptada.
- **Contexto:** El requisito de publicación exige trazabilidad y el historial debe sobrevivir al proceso de aplicación.
- **Decisión:** Registrar evento `KUDO_PUBLICADO` asociado con actor, entidad y fecha en la transacción de publicación.
- **Consecuencias:** El registro de publicación es consistente con el Kudo; los permisos de modificación/retención y la cobertura de otros eventos deben definirse para auditoría institucional completa. Auditoría de negocio complementa, no reemplaza, logs y métricas.

## ABB y SBB observados

| ABB | SBB / tecnología de la línea base |
|---|---|
| API de aplicación | Spring Boot Web MVC, controladores REST y DTO |
| Identidad y autorización | Spring Security, usuario institucional y roles |
| Persistencia transaccional | PostgreSQL, Spring Data JPA, Flyway |
| Archivos | `AlmacenamientoAdjuntos`; Supabase Storage o filesystem local |
| Notificaciones | Cola PostgreSQL, procesador `@Scheduled`, adaptadores Brevo, Meta WhatsApp y Firebase |
| Auditoría | Eventos de dominio persistidos en PostgreSQL |
| Salud operacional | Spring Boot Actuator health/info; logs SLF4J |
| Empaquetado y despliegue | Docker y configuración Render |

## Aspectos que requieren cerrar para afirmar cumplimiento completo

- Push a usuarios conectados específicamente: definir presencia/conexión o reformular como push a dispositivos registrados.
- Observabilidad: agregar métricas de negocio/proveedor y trazas; actualmente Actuator expone health/info.
- Seguridad de adjuntos: límites, validación de contenido, autorización de descarga, antivirus y estrategia de recuperación.
- DevSecOps: incorporar CI/CD, Sonar y Trivy, con evidencias de ejecución.
- Escala de lecturas: medir consultas y definir índices; añadir caché o réplica únicamente con evidencia de necesidad.
