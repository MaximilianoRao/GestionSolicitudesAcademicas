import java.util.List;

public class ReporteSolicitudes {

    public String generar(List<Solicitud> solicitudes) {
        StringBuilder texto = new StringBuilder("=== REPORTE DE SOLICITUDES ===");

        for (Solicitud s : solicitudes) {
            texto.append("\n").append(lineaDe(s));
        }

        texto.append("\n").append(resumenPorEstado(solicitudes));
        return texto.toString();
    }

    private String lineaDe(Solicitud s) {
        String responsable = "sin asignar";
        if (s.getResponsable() != null) {
            responsable = s.getResponsable().getNombre();
        }
        return s.getId() + " | " + s.getEstudiante().getNombre() + " | " + s.getTipo()
                + " | " + s.getPrioridad() + " | " + s.getEstado() + " | " + responsable;
    }

    private String resumenPorEstado(List<Solicitud> solicitudes) {
        return "Pendientes: " + contar(solicitudes, EstadoSolicitud.PENDIENTE)
                + " | En proceso: " + contar(solicitudes, EstadoSolicitud.EN_PROCESO)
                + " | Resueltas: " + contar(solicitudes, EstadoSolicitud.RESUELTA)
                + " | Rechazadas: " + contar(solicitudes, EstadoSolicitud.RECHAZADA);
    }

    private int contar(List<Solicitud> solicitudes, EstadoSolicitud estado) {
        int cantidad = 0;
        for (Solicitud s : solicitudes) {
            if (s.getEstado() == estado) {
                cantidad++;
            }
        }
        return cantidad;
    }
}
