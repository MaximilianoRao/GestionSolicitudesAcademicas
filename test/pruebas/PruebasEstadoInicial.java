import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// Pruebas de comportamiento (versión inicial, tag estado-inicial). Mismos casos que la versión refactorizada.
// En esta versión los errores se informan con códigos negativos o false.
public class PruebasEstadoInicial {
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

    private static void cp01RegistrarSolicitudValida() {
        GestorSolicitudes gestor = nuevoGestor();
        gestor.registrarSolicitud(1, "L001", "Ana Lopez", "ana@mail.com", "CONSTANCIA", "BAJA", "Constancia", "2026-11-03");
        verificar("CP01", "Registrar una solicitud válida la deja PENDIENTE",
                gestor.buscarPorEstado("PENDIENTE").size() == 1);
    }

    private static void cp02EmailInvalidoRechazado() {
        GestorSolicitudes gestor = nuevoGestor();
        int codigo = gestor.registrarSolicitud(1, "L003", "Pedro Ruiz", "pedro.mail.com", "OTRO", "BAJA", "Consulta", "2026-11-04");
        verificar("CP02", "Email sin @ se rechaza y no se registra nada",
                codigo < 0 && cantidadTotal(gestor) == 0);
    }

    private static void cp03LegajoVacioRechazado() {
        GestorSolicitudes gestor = nuevoGestor();
        int codigo = gestor.registrarSolicitud(1, "", "Ana Lopez", "ana@mail.com", "BECA", "ALTA", "Beca", "2026-11-03");
        verificar("CP03", "Legajo vacío se rechaza", codigo < 0 && cantidadTotal(gestor) == 0);
    }

    private static void cp04FechaInvalidaRechazada() {
        GestorSolicitudes gestor = nuevoGestor();
        int codigo = gestor.registrarSolicitud(1, "L001", "Ana Lopez", "ana@mail.com", "BECA", "ALTA", "Beca", "abc");
        verificar("CP04", "Fecha inválida se rechaza", codigo < 0 && cantidadTotal(gestor) == 0);
    }

    private static void cp05TransicionPendienteAEnProceso() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        boolean ok = gestor.cambiarEstado(1, "EN_PROCESO");
        verificar("CP05", "PENDIENTE -> EN_PROCESO está permitido",
                ok && gestor.buscarPorEstado("EN_PROCESO").size() == 1);
    }

    private static void cp06SaltoDePendienteAResueltaRechazado() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        boolean ok = gestor.cambiarEstado(1, "RESUELTA");
        verificar("CP06", "PENDIENTE -> RESUELTA se rechaza y el estado no cambia",
                !ok && gestor.buscarPorEstado("PENDIENTE").size() == 1);
    }

    private static void cp07TransicionesDesdeEnProceso() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        registrarBase(gestor, 2);
        gestor.cambiarEstado(1, "EN_PROCESO");
        gestor.cambiarEstado(2, "EN_PROCESO");
        boolean a = gestor.cambiarEstado(1, "RESUELTA");
        boolean b = gestor.cambiarEstado(2, "RECHAZADA");
        verificar("CP07", "EN_PROCESO -> RESUELTA y EN_PROCESO -> RECHAZADA están permitidos",
                a && b && gestor.buscarPorEstado("RESUELTA").size() == 1
                && gestor.buscarPorEstado("RECHAZADA").size() == 1);
    }

    private static void cp08SolicitudResueltaNoCambia() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        gestor.cambiarEstado(1, "EN_PROCESO");
        gestor.cambiarEstado(1, "RESUELTA");
        boolean ok = gestor.cambiarEstado(1, "EN_PROCESO");
        verificar("CP08", "Una solicitud RESUELTA no puede cambiar de estado",
                !ok && gestor.buscarPorEstado("RESUELTA").size() == 1);
    }

    private static void cp09AsignarResponsableExistente() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        boolean ok = gestor.asignarResponsable(1, "Carlos Perez");
        Responsable r = gestor.buscarPorEstado("PENDIENTE").get(0).getResponsable();
        verificar("CP09", "Asignar un responsable existente lo deja asignado",
                ok && r != null && r.getNombre().equals("Carlos Perez"));
    }

    private static void cp10AsignarEnSolicitudInexistente() {
        GestorSolicitudes gestor = nuevoGestor();
        verificar("CP10", "Asignar responsable a una solicitud inexistente se rechaza",
                !gestor.asignarResponsable(99, "Carlos Perez"));
    }

    private static void cp11AsignarResponsableInexistente() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        boolean ok = gestor.asignarResponsable(1, "Nadie");
        verificar("CP11", "Asignar un responsable inexistente se rechaza y no asigna nada",
                !ok && gestor.buscarPorEstado("PENDIENTE").get(0).getResponsable() == null);
    }

    private static void cp12Busquedas() {
        GestorSolicitudes gestor = gestorDemo();
        gestor.cambiarEstado(2, "EN_PROCESO");
        gestor.cambiarEstado(2, "RESUELTA");
        verificar("CP12", "Búsquedas: ALTA=1, PENDIENTE=2, BECA=1",
                gestor.buscarPorPrioridad("ALTA").size() == 1
                && gestor.buscarPorEstado("PENDIENTE").size() == 2
                && gestor.buscarPorTipo("BECA").size() == 1);
    }

    private static void cp13Reporte() {
        GestorSolicitudes gestor = gestorDemo();
        gestor.asignarResponsable(2, "Carlos Perez");
        gestor.cambiarEstado(2, "EN_PROCESO");
        gestor.cambiarEstado(2, "RESUELTA");
        // En la versión inicial el reporte se imprime por consola: se captura la salida.
        PrintStream original = System.out;
        ByteArrayOutputStream capturado = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturado));
        gestor.listarReporte();
        System.setOut(original);
        verificar("CP13", "El reporte tiene exactamente el contenido esperado",
                normalizar(capturado.toString()).equals(reporteEsperado()));
    }

    private static void cp14MismoEstudianteSeReutiliza() {
        GestorSolicitudes gestor = gestorConSolicitud(1);
        registrarBase(gestor, 2);
        verificar("CP14", "Dos solicitudes del mismo legajo comparten el mismo estudiante",
                gestor.buscarPorEstado("PENDIENTE").get(0).getEstudiante()
                == gestor.buscarPorEstado("PENDIENTE").get(1).getEstudiante());
    }

    // ---------- Apoyo ----------

    private static GestorSolicitudes nuevoGestor() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        gestor.agregarResponsable("Laura Gomez", "Alumnado");
        gestor.agregarResponsable("Carlos Perez", "Becas");
        return gestor;
    }

    private static void registrarBase(GestorSolicitudes gestor, int id) {
        gestor.registrarSolicitud(id, "L001", "Ana Lopez", "ana@mail.com", "CONSTANCIA", "BAJA", "Constancia", "2026-11-03");
    }

    private static GestorSolicitudes gestorConSolicitud(int id) {
        GestorSolicitudes gestor = nuevoGestor();
        registrarBase(gestor, id);
        return gestor;
    }

    private static GestorSolicitudes gestorDemo() {
        GestorSolicitudes gestor = nuevoGestor();
        gestor.registrarSolicitud(1, "L001", "Ana Lopez", "ana@mail.com", "CONSTANCIA", "BAJA", "Constancia de alumno regular", "2026-11-03");
        gestor.registrarSolicitud(2, "L002", "Juan Diaz", "juan@mail.com", "BECA", "ALTA", "Solicitud de beca", "2026-11-03");
        gestor.registrarSolicitud(3, "L001", "Ana Lopez", "ana@mail.com", "EQUIVALENCIA", "MEDIA", "Equivalencia de materia", "2026-11-04");
        return gestor;
    }

    private static int cantidadTotal(GestorSolicitudes gestor) {
        return gestor.buscarPorEstado("PENDIENTE").size() + gestor.buscarPorEstado("EN_PROCESO").size()
                + gestor.buscarPorEstado("RESUELTA").size() + gestor.buscarPorEstado("RECHAZADA").size();
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

    private static void verificar(String id, String descripcion, boolean condicion) {
        total++;
        if (!condicion) {
            fallidas++;
        }
        System.out.println(id + " " + (condicion ? "OK   " : "FALLA") + " - " + descripcion);
    }
}
