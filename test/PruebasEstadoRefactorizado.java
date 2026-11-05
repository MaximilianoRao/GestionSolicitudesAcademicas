import java.util.List;

// Pruebas de comportamiento (versión refactorizada). Patrón AAA: Arrange, Act, Assert.
public class PruebasEstadoRefactorizado {
    private static int total = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        cp01RegistrarSolicitudValida();
        cp02EmailInvalidoRechazado();
        cp03LegajoVacioRechazado();
        cp04FechaInvalidaRechazada();
        cp05TransicionPendienteAEnProceso();
        cp06SaltoDePendienteAResueltaRechazado();
        cp07TransicionesDesdeEnProceso();
        cp08SolicitudResueltaNoCambia();
        cp09AsignarResponsableExistente();
        cp10AsignarEnSolicitudInexistente();
        cp11AsignarResponsableInexistente();
        cp12Busquedas();
        cp13Reporte();
        cp14MismoEstudianteSeReutiliza();

        System.out.println("\nResultado: " + (total - fallidas) + "/" + total + " pruebas OK");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    // ---------- Casos de prueba ----------

    private static void cp01RegistrarSolicitudValida() {
        GestorSolicitudes gestor = nuevoGestor();                       // Arrange
        gestor.registrarSolicitud(1, ana(), TipoSolicitud.CONSTANCIA,
                PrioridadSolicitud.BAJA, "Constancia", "2026-11-03");   // Act
        verificar("CP01", "Registrar una solicitud válida la deja PENDIENTE",
                gestor.listarSolicitudes().size() == 1
                && gestor.listarSolicitudes().get(0).getEstado() == EstadoSolicitud.PENDIENTE); // Assert
    }

    private static void cp02EmailInvalidoRechazado() {
        GestorSolicitudes gestor = nuevoGestor();
        boolean rechazada = lanzaSolicitudException(() -> gestor.registrarSolicitud(1,
                new DatosEstudiante("L003", "Pedro Ruiz", "pedro.mail.com"),
                TipoSolicitud.OTRO, PrioridadSolicitud.BAJA, "Consulta", "2026-11-04"));
        verificar("CP02", "Email sin @ se rechaza y no se registra nada",
                rechazada && gestor.listarSolicitudes().isEmpty());
    }

    private static void cp03LegajoVacioRechazado() {
        GestorSolicitudes gestor = nuevoGestor();
        boolean rechazada = lanzaSolicitudException(() -> gestor.registrarSolicitud(1,
                new DatosEstudiante("", "Ana Lopez", "ana@mail.com"),
                TipoSolicitud.BECA, PrioridadSolicitud.ALTA, "Beca", "2026-11-03"));
        verificar("CP03", "Legajo vacío se rechaza", rechazada && gestor.listarSolicitudes().isEmpty());
    }

    private static void cp04FechaInvalidaRechazada() {
        GestorSolicitudes gestor = nuevoGestor();
        boolean rechazada = lanzaSolicitudException(() -> gestor.registrarSolicitud(1, ana(),
                TipoSolicitud.BECA, PrioridadSolicitud.ALTA, "Beca", "abc"));
        verificar("CP04", "Fecha inválida se rechaza", rechazada && gestor.listarSolicitudes().isEmpty());
    }

    private static void cp05TransicionPendienteAEnProceso() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        gestor.cambiarEstado(1, EstadoSolicitud.EN_PROCESO);
        verificar("CP05", "PENDIENTE -> EN_PROCESO está permitido",
                gestor.listarSolicitudes().get(0).getEstado() == EstadoSolicitud.EN_PROCESO);
    }

    private static void cp06SaltoDePendienteAResueltaRechazado() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        boolean rechazada = lanzaSolicitudException(() -> gestor.cambiarEstado(1, EstadoSolicitud.RESUELTA));
        verificar("CP06", "PENDIENTE -> RESUELTA se rechaza y el estado no cambia",
                rechazada && gestor.listarSolicitudes().get(0).getEstado() == EstadoSolicitud.PENDIENTE);
    }

    private static void cp07TransicionesDesdeEnProceso() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        registrarBase(gestor, 2);
        gestor.cambiarEstado(1, EstadoSolicitud.EN_PROCESO);
        gestor.cambiarEstado(2, EstadoSolicitud.EN_PROCESO);
        gestor.cambiarEstado(1, EstadoSolicitud.RESUELTA);
        gestor.cambiarEstado(2, EstadoSolicitud.RECHAZADA);
        List<Solicitud> todas = gestor.listarSolicitudes();
        verificar("CP07", "EN_PROCESO -> RESUELTA y EN_PROCESO -> RECHAZADA están permitidos",
                todas.get(0).getEstado() == EstadoSolicitud.RESUELTA
                && todas.get(1).getEstado() == EstadoSolicitud.RECHAZADA);
    }

    private static void cp08SolicitudResueltaNoCambia() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        gestor.cambiarEstado(1, EstadoSolicitud.EN_PROCESO);
        gestor.cambiarEstado(1, EstadoSolicitud.RESUELTA);
        boolean rechazada = lanzaSolicitudException(() -> gestor.cambiarEstado(1, EstadoSolicitud.EN_PROCESO));
        verificar("CP08", "Una solicitud RESUELTA no puede cambiar de estado",
                rechazada && gestor.listarSolicitudes().get(0).getEstado() == EstadoSolicitud.RESUELTA);
    }

    private static void cp09AsignarResponsableExistente() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        gestor.asignarResponsable(1, "Carlos Perez");
        Responsable r = gestor.listarSolicitudes().get(0).getResponsable();
        verificar("CP09", "Asignar un responsable existente lo deja asignado",
                r != null && r.getNombre().equals("Carlos Perez"));
    }

    private static void cp10AsignarEnSolicitudInexistente() {
        GestorSolicitudes gestor = nuevoGestor();
        verificar("CP10", "Asignar responsable a una solicitud inexistente se rechaza",
                lanzaSolicitudException(() -> gestor.asignarResponsable(99, "Carlos Perez")));
    }

    private static void cp11AsignarResponsableInexistente() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        boolean rechazada = lanzaSolicitudException(() -> gestor.asignarResponsable(1, "Nadie"));
        verificar("CP11", "Asignar un responsable inexistente se rechaza y no asigna nada",
                rechazada && gestor.listarSolicitudes().get(0).getResponsable() == null);
    }

    private static void cp12Busquedas() {
        GestorSolicitudes gestor = gestorDemo();
        gestor.cambiarEstado(2, EstadoSolicitud.EN_PROCESO);
        gestor.cambiarEstado(2, EstadoSolicitud.RESUELTA);
        verificar("CP12", "Búsquedas: ALTA=1, PENDIENTE=2, BECA=1",
                gestor.buscarPorPrioridad(PrioridadSolicitud.ALTA).size() == 1
                && gestor.buscarPorEstado(EstadoSolicitud.PENDIENTE).size() == 2
                && gestor.buscarPorTipo(TipoSolicitud.BECA).size() == 1);
    }

    private static void cp13Reporte() {
        GestorSolicitudes gestor = gestorDemo();
        gestor.asignarResponsable(2, "Carlos Perez");
        gestor.cambiarEstado(2, EstadoSolicitud.EN_PROCESO);
        gestor.cambiarEstado(2, EstadoSolicitud.RESUELTA);
        String reporte = new ReporteSolicitudes().generar(gestor.listarSolicitudes());
        verificar("CP13", "El reporte tiene exactamente el contenido esperado",
                normalizar(reporte).equals(reporteEsperado()));
    }

    private static void cp14MismoEstudianteSeReutiliza() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        registrarBase(gestor, 2);
        List<Solicitud> todas = gestor.listarSolicitudes();
        verificar("CP14", "Dos solicitudes del mismo legajo comparten el mismo estudiante",
                todas.get(0).getEstudiante() == todas.get(1).getEstudiante());
    }

    // ---------- Apoyo (Arrange común) ----------

    private static GestorSolicitudes nuevoGestor() {
        GestorSolicitudes gestor = new GestorSolicitudes(new RepositorioSolicitudesMemoria());
        gestor.agregarResponsable("Laura Gomez", "Alumnado");
        gestor.agregarResponsable("Carlos Perez", "Becas");
        return gestor;
    }

    private static DatosEstudiante ana() {
        return new DatosEstudiante("L001", "Ana Lopez", "ana@mail.com");
    }

    private static void registrarBase(GestorSolicitudes gestor, int id) {
        gestor.registrarSolicitud(id, ana(), TipoSolicitud.CONSTANCIA,
                PrioridadSolicitud.BAJA, "Constancia", "2026-11-03");
    }

    private static GestorSolicitudes gestorConSolicitud(int id) {
        GestorSolicitudes gestor = nuevoGestor();
        registrarBase(gestor, id);
        return gestor;
    }

    // Mismos datos que usa Main
    private static GestorSolicitudes gestorDemo() {
        GestorSolicitudes gestor = nuevoGestor();
        gestor.registrarSolicitud(1, ana(), TipoSolicitud.CONSTANCIA, PrioridadSolicitud.BAJA,
                "Constancia de alumno regular", "2026-11-03");
        gestor.registrarSolicitud(2, new DatosEstudiante("L002", "Juan Diaz", "juan@mail.com"),
                TipoSolicitud.BECA, PrioridadSolicitud.ALTA, "Solicitud de beca", "2026-11-03");
        gestor.registrarSolicitud(3, ana(), TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.MEDIA,
                "Equivalencia de materia", "2026-11-04");
        return gestor;
    }

    private static String reporteEsperado() {
        return "=== REPORTE DE SOLICITUDES ===\n"
                + "1 | Ana Lopez | CONSTANCIA | BAJA | PENDIENTE | sin asignar\n"
                + "2 | Juan Diaz | BECA | ALTA | RESUELTA | Carlos Perez\n"
                + "3 | Ana Lopez | EQUIVALENCIA | MEDIA | PENDIENTE | sin asignar\n"
                + "Pendientes: 2 | En proceso: 0 | Resueltas: 1 | Rechazadas: 0";
    }

    private static String normalizar(String texto) {
        return texto.replace("\r\n", "\n").trim();
    }

    private static boolean lanzaSolicitudException(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (SolicitudException e) {
            return true;
        }
    }

    private static void verificar(String id, String descripcion, boolean condicion) {
        total++;
        if (!condicion) {
            fallidas++;
        }
        System.out.println(id + " " + (condicion ? "OK   " : "FALLA") + " - " + descripcion);
    }
}
