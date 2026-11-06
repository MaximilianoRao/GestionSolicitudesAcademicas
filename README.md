# Gestión de Solicitudes Académicas

Trabajo Práctico Integrador de **Metodología de Sistemas II** (Tecnicatura Universitaria en Programación a Distancia).

El objetivo del trabajo no es construir un sistema grande, sino **analizar y mejorar un módulo existente**: diagnosticar su calidad interna, reducir la deuda técnica con refactorings seguros, verificar que el comportamiento externo se mantiene y revisar el resultado con un checklist de code review.

## Descripción

Módulo que permite registrar solicitudes de estudiantes (constancias, equivalencias, becas u otras), hacerles seguimiento hasta su resolución y generar un reporte. Se ejecuta por consola, con datos sintéticos, y guarda los datos en memoria.

El repositorio contiene dos versiones del mismo sistema:

- **Versión inicial** (`estado-inicial`): funciona, pero con problemas de diseño detectables a propósito.
- **Versión refactorizada** (`estado-refactorizado`): mismo comportamiento externo, con mejor estructura interna.

## Funcionalidades

- Registrar una solicitud (valida legajo, email, tipo, prioridad y fecha).
- Cambiar el estado: `PENDIENTE` → `EN_PROCESO` → `RESUELTA` o `RECHAZADA`.
- Asignar un responsable a una solicitud.
- Buscar solicitudes por estado, prioridad o tipo.
- Generar un reporte con el detalle y los totales por estado.

## Tecnologías

- Java (JDK 8 o superior).
- Git y GitHub para el control de versiones.
- Datos en memoria (listas), sin base de datos ni librerías externas.

## Estructura del repositorio

- `src/`: código fuente (`GestorSolicitudes`, `RepositorioSolicitudes`, `RepositorioSolicitudesMemoria`, `ReporteSolicitudes`, entidades, enums, `SolicitudException` y `Main`).
- `test/`: pruebas de comportamiento de la versión refactorizada.
- `docs/`: documentación del trabajo.
  - `diagnostico-code-smells.md`: diagnóstico de deuda técnica.
  - `checklist-code-review.md`: checklist de code review, observaciones y criterios de aceptación.

## Instalación y uso

Requisito: tener instalado el JDK.

Compilar y ejecutar el sistema:

```
javac -d bin src/*.java
java -cp bin Main
```

## Pruebas

Las pruebas están escritas en Java puro (patrón Arrange-Act-Assert) y se ejecutan así:

```
javac -d bin src/*.java test/*.java
java -cp bin PruebasEstadoRefactorizado
```

Cada caso informa `OK` o `FALLA` y al final se muestra el total de pruebas aprobadas.

## Versiones (tags)

- `estado-inicial`: versión base con problemas de diseño.
- `etapa-diagnostico`: diagnóstico de deuda técnica y code smells.
- `etapa-refactoring`: refactorings aplicados.
- `etapa-pruebas`: pruebas antes/después.
- `etapa-docs`: documentación y code review.
- `estado-refactorizado`: estado final del código.

Para ver una versión: `git checkout <tag>`.

## Integrantes

- Tomas Jose Buforn
- Maximiliano Rao

