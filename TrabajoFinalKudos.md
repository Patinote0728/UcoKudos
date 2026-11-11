# PROYECTO INTEGRADOR FINAL
# Ingeniería del Software II
# Plataforma Institucional de Reconocimientos UCO (UCO Kudos)

---

# Información General

| Campo | Valor |
|---------|---------|
| Curso | Ingeniería del Software II |
| Proyecto | Proyecto Integrador Final |
| Modalidad | Individual |
| Fecha límite de entrega | Viernes 13 de noviembre de 2026 |
| Hora límite | 12:00 m. |
| Sustentación | Obligatoria |
| Formato documental | Markdown (.md) |
| Entregables técnicos | Front-End + Back-End |
| Estado esperado a la fecha límite | Entregado, desplegado y sustentado |

---

# Introducción

La Universidad Católica de Oriente (UCO) desea evolucionar su plataforma institucional de reconocimientos denominada **UCO Kudos** hacia una solución empresarial que permita impulsar la cultura organizacional, fomentar el reconocimiento institucional y fortalecer la participación activa de la comunidad académica.

El proyecto requiere el diseño arquitectónico completo de la solución, la construcción de una línea base ejecutable y la materialización de una arquitectura capaz de responder a necesidades reales de escalabilidad, observabilidad, seguridad, mantenibilidad y evolución futura.

El objetivo principal de este ejercicio no consiste únicamente en construir software funcional, sino en demostrar la capacidad para realizar diseño de alto nivel, diseño detallado, construcción de línea base, automatización y gobierno técnico de una solución empresarial.

---

# Objetivo General

Diseñar, documentar e implementar una línea base arquitectónica funcional para la plataforma institucional UCO Kudos aplicando principios de Arquitectura de Software, Ingeniería del Software, Diseño Orientado a Objetos, DevSecOps, Observabilidad y Calidad de Software.

---

# Alcance del Proyecto

El estudiante deberá:

- Definir arquitectura objetivo.
- Definir diseño de alto nivel.
- Definir diseño detallado.
- Identificar drivers arquitectónicos.
- Identificar atributos de calidad.
- Identificar ABB (Architecture Building Blocks).
- Seleccionar SBB (Solution Building Blocks).
- Implementar la línea base.
- Automatizar calidad.
- Automatizar seguridad.
- Automatizar despliegue.
- Publicar la solución.
- Sustentar las decisiones tomadas.

---

# Estructura Organizacional

La plataforma deberá soportar la siguiente estructura organizacional:

```text
Administrador del Sistema
        │
        ▼
      Rector
        │
        ▼
Coordinador Académico
        │
        ▼
Coordinador de Facultad (Decano)
        │
        ▼
Coordinador de Programa
        │
        ▼
Docentes y Estudiantes
```

---

# Roles Institucionales

La plataforma debe soportar:

- Administrador del Sistema.
- Rector.
- Coordinador Académico.
- Coordinador de Facultad.
- Coordinador de Programa.
- Docente.
- Estudiante.

---

# Restricciones Organizacionales

## Exclusividad

Cada persona podrá poseer únicamente un rol activo.

---

## Unicidad

La Universidad tendrá:

- Un Rector activo.
- Un Coordinador Académico activo.

Cada Facultad tendrá:

- Un Coordinador de Facultad activo.

Cada Programa Académico tendrá:

- Un Coordinador de Programa activo.

---

# Registro y Activación

Todos los usuarios deberán registrarse utilizando:

- Correo institucional.
- Número celular.

La cuenta sólo podrá quedar activa cuando:

```text
Solicitud aprobada
+
Correo verificado
+
Celular verificado
```

---

# Flujo de Aprobaciones

| Solicitud | Aprobador |
|------------|------------|
| Estudiante | Coordinador de Programa |
| Coordinador de Programa | Coordinador de Facultad |
| Coordinador de Facultad | Coordinador Académico |
| Coordinador Académico | Rector |
| Rector | Administrador |
| Administrador | Administrador |

---

# Gestión Académica

## Estudiantes

- Un estudiante puede estudiar varios programas académicos.
- Debe ser posible conocer qué programas estudia un estudiante.

## Docentes

- Un docente puede enseñar en múltiples programas.
- Debe ser posible conocer en qué programas enseña un docente.

## Coordinadores de Programa

- Debe poder conocerse qué programa académico acompañan.
- Administran estudiantes de sus programas.
- Administran docentes de sus programas.

## Coordinadores de Facultad

- Debe poder conocerse qué facultad acompañan.
- Administran coordinadores de programa pertenecientes a su facultad.

## Coordinadores Académicos

- Administran coordinadores de facultad.

## Rector

- Administra coordinadores académicos.

## Administradores

- Administran administradores y rectores.

---

# Categorías de Kudos

La plataforma deberá permitir como mínimo:

- Rendimiento Académico.
- Mejor Investigador.
- Empatía.
- Comunicación Asertiva.
- Orientación al Logro.
- Trabajo en Equipo.
- Trabajo Bajo Presión.
- Visión Estratégica.
- Innovación.
- Liderazgo.
- Transformación Digital.
- Mentor Inspirador.
- Espíritu UCO.
- Servicio a la Comunidad.
- Compromiso Institucional.

---

# Administración de Categorías

Únicamente podrán administrar categorías:

- Administrador del Sistema.
- Rector.
- Coordinador Académico.

Los demás roles no podrán administrar categorías.

---

# Gestión de Kudos

Todo Kudo deberá permitir:

- Autor.
- Destinatario.
- Categoría.
- Fecha.
- Contenido descriptivo.
- Adjuntos.

El contenido no tendrá límite de longitud.

---

# Archivos Adjuntos

Los Kudos podrán contener archivos adjuntos.

Los estudiantes deberán diseñar:

- Estrategia de almacenamiento.
- Estrategia de seguridad.
- Estrategia de recuperación.
- Estrategia de trazabilidad.

---

# Réplicas

La plataforma permitirá:

- Réplicas sobre Kudos.
- Réplicas sobre otras Réplicas.

Restricciones:

- Sin límite de profundidad.
- Sin límite de cantidad.
- Sin límite de longitud.

---

# Calificaciones

Los usuarios podrán calificar:

- Kudos.
- Réplicas.

Escala:

```text
1 a 5 estrellas
```

Un usuario únicamente podrá tener una calificación activa sobre el mismo contenido.

---

# Moderación

Todos los usuarios podrán reportar contenido.

Únicamente podrán vetar:

- Rector.
- Coordinador Académico.

No podrán vetar:

- Administradores.
- Coordinadores de Facultad.
- Coordinadores de Programa.
- Docentes.
- Estudiantes.

---

# Funcionalidad Obligatoria de Línea Base

La línea base deberá implementar completamente el flujo:

## Publicar Kudo

Cuando una persona publique un Kudo:

### Paso 1

El Kudo deberá almacenarse correctamente.

### Paso 2

El destinatario deberá recibir un correo electrónico.

### Paso 3

El destinatario deberá recibir una notificación de WhatsApp.

### Paso 4

Si el destinatario se encuentra conectado a la aplicación deberá recibir una notificación PUSH en tiempo real.

### Paso 5

Todo el proceso deberá quedar auditado.

---

# Drivers Arquitectónicos

## DRV-001 Movilidad

Se estima que aproximadamente el 90% de los usuarios utilizará la plataforma desde dispositivos móviles.

La estrategia de diseño deberá ser Mobile First.

---

## DRV-002 Alta Demanda de Lectura

Se espera una carga predominantemente orientada a lectura.

La solución deberá justificar cómo responde a escenarios donde la mayoría de las operaciones sean consultas.

---

## DRV-003 Notificaciones Multicanal

La plataforma deberá soportar:

- Correo electrónico.
- WhatsApp.
- Push Notifications.

---

## DRV-004 Adjuntos

Los Kudos deberán soportar carga de archivos.

---

## DRV-005 Seguridad

La arquitectura debe incorporar mecanismos de seguridad apropiados.

---

## DRV-006 Observabilidad

La arquitectura debe incorporar capacidades de monitoreo, métricas, trazas y auditoría.

---

## DRV-007 Automatización

La arquitectura debe incorporar capacidades DevSecOps desde la línea base.

---

# Diseño

Los estudiantes deberán entregar:

- Diagrama de nivel 1.
- Diagrama de nivel 2.
- Diagrama de componentes.
- Diagrama de paquetes.
- Diagramas de Secuencia principales.
- Drivers arquitectónicos.
- ADR's

---

# Architecture Building Blocks (ABB)

El estudiante deberá identificar y justificar los ABB necesarios para la solución.

Ejemplos posibles:

- Gestión de Identidad.
- Autenticación.
- Autorización.
- Persistencia.
- Gestión Documental y de archivos.
- Notificaciones.
- Mensajería.
- Auditoría.
- Logging.
- Observabilidad.
- Gestión de Configuración.
- Seguridad.
- DevSecOps.

---

# Solution Building Blocks (SBB)

El estudiante deberá seleccionar y justificar los SBB utilizados para materializar cada ABB.

La selección deberá considerar:

- Costos.
- Complejidad.
- Escalabilidad.
- Riesgos.
- Mantenibilidad.
- Evolución futura.

---

# Clean Architecture

La solución deberá demostrar aplicación de Clean Architecture.

Se deberá explicar:

- Capas.
- Dependencias.
- Responsabilidades.
- Reglas arquitectónicas.

---

# Reactive Manifesto

La solución deberá analizar y justificar cómo aborda:

- Responsividad.
- Resiliencia.
- Elasticidad.
- Orientación a mensajes.

---

# Twelve-Factor App

La documentación deberá contener una matriz de evaluación de cumplimiento de los doce factores.

Cada factor deberá ser:

- Analizado.
- Evaluado.
- Justificado.

---

# Clean Code

La solución deberá evidenciar:

- Nombres expresivos.
- Métodos cohesivos.
- Responsabilidades claras.
- Bajo acoplamiento.
- Alta cohesión.
- Código mantenible.

---

# Patrones de Diseño

Los estudiantes deberán identificar y justificar los patrones utilizados.

No se evaluará la cantidad.

Se evaluará:

- Pertinencia.
- Correcta aplicación.
- Beneficio obtenido.

---

# Desarrollo Seguro

La solución deberá incluir análisis y justificación sobre:

- Autenticación.
- Autorización.
- Gestión de secretos.
- Validaciones.
- APIs.
- Integraciones con terceros.
- Riesgos de seguridad.

---

# Pirámide de Pruebas

La solución deberá incorporar pruebas automatizadas.

Como mínimo:

## Unitarias

Pruebas de componentes aislados.

## Integración

Pruebas entre componentes.

## End-to-End

Pruebas de escenarios completos.

---

# DevSecOps Obligatorio

La solución deberá incorporar:

## CI

Pipeline de Integración Continua.

## CD

Pipeline de Despliegue Automatizado.

## Sonar

Análisis estático obligatorio.

## Trivy

Escaneo de vulnerabilidades obligatorio.

---

# Publicación Obligatoria

La solución deberá encontrarse publicada.

El docente deberá poder:

- Acceder mediante URL pública.
- Utilizar el sistema.
- Validar el flujo de publicación de Kudos.

---

# Entregables

## Documentación

Toda la documentación deberá ser entregada en:

```text
Markdown (.md)
```

---

## Repositorio Front-End

Obligatorio.

---

## Repositorio Back-End

Obligatorio.

---

## Manual de Puesta a Punto

Obligatorio.

---

## Evidencias

Obligatorias.

---

# Evidencias Obligatorias

Los estudiantes deberán entregar evidencias de:

- CI.
- CD.
- Sonar.
- Trivy.
- Publicación.
- Despliegue.
- Correo electrónico.
- WhatsApp.
- Push Notifications.
- Auditoría.
- Observabilidad.
- Pruebas.

---

# Uso de Inteligencia Artificial

Se permite el uso responsable de herramientas de IA.

Sin embargo:

- Todo debe ser comprendido.
- Todo debe poder ser explicado.
- Todo debe poder ser sustentado.

Durante la sustentación podrán solicitarse:

- Cambios de código.
- Refactorizaciones.
- Implementaciones en vivo.
- Corrección de errores.

---

# Modalidad

Este trabajo deberá desarrollarse de forma:

```text
INDIVIDUAL
```

---

# Sustentación

La sustentación es obligatoria.

Cada estudiante deberá sustentar individualmente y hacerlo máximo hasta la fecha de entrega definida.

---

# Condición de Entrega Final

Máximo hasta las **12:00 del mediodía del viernes 13 de noviembre de 2026** deberá cumplirse simultáneamente:

✅ Documentación entregada.

✅ Front-End entregado.

✅ Back-End entregado.

✅ Manual entregado.

✅ Evidencias entregadas.

✅ Aplicación desplegada.

✅ URL pública funcional.

✅ Sonar ejecutado.

✅ Trivy ejecutado.

✅ Pipeline CI funcional.

✅ Pipeline CD funcional.

✅ Flujo de publicación de Kudo implementado.

✅ Correo funcionando.

✅ WhatsApp funcionando.

✅ Push Notifications funcionando.

✅ Sustentación realizada.

---

# Aplicabilidad

Estas condiciones aplican para:

- Todos los estudiantes que deban presentar evaluación final.

---

# Resultado Esperado

Al finalizar el proyecto el estudiante deberá entregar:

- Arquitectura objetivo.
- Diseño detallado.
- Línea base ejecutable.
- Plataforma desplegada.
- Automatización DevSecOps.
- Evidencias de calidad.
- Evidencias de seguridad.
- Evidencias de observabilidad.
- Solución funcional sustentada.

El resultado deberá demostrar la correcta aplicación de Ingeniería del Software, Arquitectura de Software, Diseño Orientado a Objetos, Clean Architecture, Sistemas Reactivos, Twelve-Factor App, DevSecOps, Desarrollo Seguro y Automatización de Pruebas.