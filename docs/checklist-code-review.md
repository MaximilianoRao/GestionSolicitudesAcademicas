# Code review – Sistema de Gestión de Solicitudes Académicas

- **Versión revisada:** versión refactorizada (commit/tag: 8fe5bb9)
- **Autor del código:** Maximiliano Rao
- **Revisor:** Tomas Jose Buforn
- **Fecha de la revisión:** 06/11/2026
- **Alcance:** los 13 archivos de `src/` y la suite de pruebas (`PruebasEstadoRefactorizado`).
- **Categorías del checklist:** lógica y funcionalidad, legibilidad, diseño y arquitectura, manejo de errores, seguridad, rendimiento, pruebas, documentación y repositorio.

Convención: &#9745; cumple · &#x26A0; observación (se detalla en la sección 2).

---

## 1. Checklist técnico aplicado

### A. Lógica y funcionalidad
- &#9745; Las operaciones principales (registrar, cambiar estado, asignar responsable, buscar y reporte) funcionan y están cubiertas por pruebas (CP01 a CP14 en verde).
- &#9745; Se validan legajo, email, tipo, prioridad y fecha antes de registrar una solicitud.
- &#x26A0; Casos de borde sin controlar: id repetido (OBS-01), datos de estudiante nulos (OBS-02) y email con validación mínima (OBS-06).

### B. Legibilidad
- &#9745; Nombres descriptivos: `buscarSolicitud`, `filtrarSolicitudes`, `permiteTransicion`, `lineaDe`, `resumenPorEstado`.
- &#9745; Métodos cortos y con una sola tarea (`validarDatosSolicitud`, `contar`, `lineaDe`).
- &#x26A0; Clases duplicadas `Estudiante` y `DatosEstudiante` (OBS-03).

### C. Diseño y arquitectura
- &#9745; SRP: casos de uso (`GestorSolicitudes`), almacenamiento (`RepositorioSolicitudes`) y presentación (`ReporteSolicitudes`) están separados.
- &#9745; DIP e ISP: `GestorSolicitudes` depende de la interfaz `RepositorioSolicitudes`, de 3 métodos, y recibe la implementación en memoria por constructor.
- &#9745; OCP: estado, tipo y prioridad son enums, las reglas de transición viven en `EstadoSolicitud` y los filtros usan `Predicate`.
- &#9745; LSP: no aplica, porque el sistema no tiene jerarquías de herencia.
- &#x26A0; `GestorSolicitudes` sigue guardando las listas de estudiantes y responsables (OBS-04).
- &#x26A0; `Solicitud.setEstado` es público y permite saltear la regla de transición (OBS-05).

### D. Manejo de errores
- &#9745; Los errores se informan con `SolicitudException` y un mensaje claro.
- &#9745; No quedan `catch` vacíos ni códigos de error numéricos.

### E. Seguridad
- &#9745; Se usan datos sintéticos; no hay datos personales reales ni credenciales.
- &#9745; El sistema no recibe entradas externas (no usa red, base de datos ni archivos), por lo que no hay superficie de inyección.

### F. Rendimiento
- &#9745; El volumen de datos es acotado y las búsquedas lineales son adecuadas para el alcance.
- &#x26A0; Menor: el bucle que busca al estudiante por legajo no corta al encontrarlo (OBS-08).

### G. Pruebas
- &#9745; 14 casos de prueba con patrón AAA, ejecutados antes y después del refactoring (13/14 y 14/14).
- &#9745; Cada caso usa una instancia nueva del gestor, sin estado compartido.
- &#x26A0; No se midió cobertura de código (limitación aceptada para el alcance del trabajo).

### H. Documentación y repositorio
- &#9745; `docs/diagnostico-code-smells.md` documenta el diagnóstico.
- &#x26A0; Verificar que existan `README.md` y las decisiones técnicas en `docs/` (OBS-09).
- &#x26A0; Verificar que el historial tenga commits atómicos (uno por refactoring) y que `bin/` no esté versionado.

---

## 2. Observaciones y feedback

**OBS-01 – Id de solicitud repetido** (prioridad: Alta)
- Ubicación: `GestorSolicitudes.registrarSolicitud`.
- Hallazgo: se pueden registrar dos solicitudes con el mismo id; las operaciones por id afectan solo a la primera.
- Feedback: validar la unicidad del id al registrar, igual que el resto de las validaciones.
- Estado: **corregida** (ver sección 3).

**OBS-02 – Datos de estudiante nulos** (prioridad: Media)
- Ubicación: `GestorSolicitudes.registrarSolicitud`.
- Hallazgo: con `datosEstudiante == null` se produce una `NullPointerException` en lugar de una `SolicitudException`.
- Feedback: mantener un único tipo de error controlado para los datos inválidos.
- Estado: **corregida** (ver sección 3).

**OBS-03 – Clases duplicadas** (prioridad: Media)
- Ubicación: `Estudiante.java` y `DatosEstudiante.java`.
- Hallazgo: ambas tienen los mismos atributos y métodos.
- Feedback: usar una sola clase, por ejemplo pasando el `Estudiante` directamente a `registrarSolicitud`.
- Estado: pendiente (mejora futura).

**OBS-04 – Responsabilidad residual en el gestor** (prioridad: Media)
- Ubicación: `GestorSolicitudes` (listas `estudiantes` y `responsables`).
- Hallazgo: el gestor coordina casos de uso y además guarda estudiantes y responsables.
- Feedback: extraer repositorios análogos al de solicitudes.
- Estado: pendiente (mejora futura).

**OBS-05 – Encapsulamiento del estado** (prioridad: Media)
- Ubicación: `Solicitud.setEstado`.
- Hallazgo: cualquier clase puede cambiar el estado sin pasar por `permiteTransicion`.
- Feedback: que `Solicitud` exponga un único método `cambiarEstado` que valide y modifique.
- Estado: pendiente (mejora futura).

**OBS-06 – Validación de email mínima** (prioridad: Baja)
- Ubicación: `GestorSolicitudes.validarDatosSolicitud`.
- Hallazgo: se acepta cualquier texto que contenga `@`, por ejemplo solo `@`.
- Feedback: endurecer la regla si el dominio lo requiere.
- Estado: pendiente (mejora futura).

**OBS-07 – Fecha como texto** (prioridad: Baja)
- Ubicación: `Solicitud.fecha` y `validarDatosSolicitud`.
- Hallazgo: la fecha se valida con `LocalDate.parse` pero se guarda como `String`.
- Feedback: guardarla como `LocalDate`.
- Estado: pendiente (mejora futura).

**OBS-08 – Bucle sin corte** (prioridad: Baja)
- Ubicación: `GestorSolicitudes.registrarSolicitud` (búsqueda de estudiante).
- Hallazgo: el `for` sigue recorriendo después de encontrar al estudiante.
- Feedback: cortar con `break` o extraer un método de búsqueda.
- Estado: pendiente (mejora futura).


**OBS-09 – Documentación del repositorio** (prioridad: Media)
- Ubicación: raíz del repositorio y `docs/`.
- Hallazgo: `README.md` genérico de VScode y bin/ en el repositorio.
- Feedback: Actualizar `README.md` y sacar bin/ del repositorio.
- Estado: se completa en la Etapa 8.

---

## 3. Correcciones realizadas

**Cambio** (al inicio de `GestorSolicitudes.registrarSolicitud`, antes de `String legajo = ...`):

```java
if (datosEstudiante == null) {
    throw new SolicitudException("Los datos del estudiante son obligatorios");
}

if (repositorio.buscarPorId(id).isPresent()) {
    throw new SolicitudException("Ya existe una solicitud con id " + id);
}
```

**Pruebas nuevas** (solo aplican a la versión corregida, no forman parte de la comparación antes/después):
- CP15: registrar un id que ya existe se rechaza y no duplica la solicitud.
- CP16: datos de estudiante nulos se rechazan con `SolicitudException`.

**Verificación:** la suite completa pasa 16/16 y la salida de `Main` no cambia.

---

## 4. Criterios de aceptación técnica (TAC)

- &#9745; El código compila y pasa todos los tests (16/16).
- &#9745; Se respetan los principios de diseño (SRP, OCP, DIP e ISP; LSP no aplica).
- &#9745; Incluye pruebas.
- &#9745; No introduce bugs ni rompe funcionalidades: el comportamiento externo se mantiene (solo mejora el CP04).
- &#9745; Manejo correcto de errores y excepciones.
- &#x26A0; No genera deuda técnica innecesaria: quedan OBS-03 a OBS-08 registradas como mejoras futuras.
- &#9745; Seguridad y buenas prácticas (datos sintéticos).
- &#x26A0; Documentación: pendiente de verificar (OBS-09).
- &#x26A0; Atomicidad: verificar un commit por cambio.

**Veredicto:** aprobado con observaciones. Las observaciones de prioridad Alta están corregidas; las restantes quedan documentadas como mejoras futuras.
