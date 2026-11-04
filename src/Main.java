import java.util.List;

public class Main {
    public static void main(String[] args) {
        GestorSolicitudes gestor = new GestorSolicitudes();
        gestor.agregarResponsable("Laura Gomez", "Alumnado");
        gestor.agregarResponsable("Carlos Perez", "Becas");

        ejecutar("Registro 1", () -> gestor.registrarSolicitud(1, "L001", "Ana Lopez", "ana@mail.com", "CONSTANCIA", "BAJA", "Constancia de alumno regular", "2026-11-03"));
        ejecutar("Registro 2", () -> gestor.registrarSolicitud(2, "L002", "Juan Diaz", "juan@mail.com", "BECA", "ALTA", "Solicitud de beca", "2026-11-03"));
        ejecutar("Registro 3", () -> gestor.registrarSolicitud(3, "L001", "Ana Lopez", "ana@mail.com", "EQUIVALENCIA", "MEDIA", "Equivalencia de materia", "2026-11-04"));
        ejecutar("Registro con email invalido", () -> gestor.registrarSolicitud(4, "L003", "Pedro Ruiz", "pedro.mail.com", "OTRO", "BAJA", "Consulta", "2026-11-04"));

        ejecutar("Asignar", () -> gestor.asignarResponsable(2, "Carlos Perez"));
        ejecutar("Estado 2 -> EN_PROCESO", () -> gestor.cambiarEstado(2, "EN_PROCESO"));
        ejecutar("Estado 2 -> RESUELTA", () -> gestor.cambiarEstado(2, "RESUELTA"));
        ejecutar("Estado 1 -> RESUELTA (salto invalido)", () -> gestor.cambiarEstado(1, "RESUELTA"));

        List<Solicitud> altas = gestor.buscarPorPrioridad("ALTA");
        System.out.println("Solicitudes ALTA: " + altas.size());
        System.out.println("Solicitudes PENDIENTE: " + gestor.buscarPorEstado("PENDIENTE").size());

        gestor.listarReporte();
    }

    private static void ejecutar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println(descripcion + ": OK");
        } catch (SolicitudException e) {
            System.out.println(descripcion + ": RECHAZADO (" + e.getMessage() + ")");
        }
    }
}