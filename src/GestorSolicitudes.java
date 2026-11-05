import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

public class GestorSolicitudes {
    private List<Solicitud> solicitudes = new ArrayList<>();
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Responsable> responsables = new ArrayList<>();

    public void agregarResponsable(String nombre, String area) {
        responsables.add(new Responsable(nombre, area));
    }

    // Registra la solicitud o lanza SolicitudException si los datos son inválidos.
    public int registrarSolicitud(int id, DatosEstudiante datosEstudiante,
                                  TipoSolicitud tipo, PrioridadSolicitud prioridad,
                                  String descripcion, String fecha) {
        String legajo = datosEstudiante.getLegajo();
        String nombreEstudiante = datosEstudiante.getNombre();
        String email = datosEstudiante.getEmail();

        validarDatosSolicitud(legajo, email, tipo, prioridad, fecha);

        Estudiante estudiante = null;

        for (Estudiante e : estudiantes) {
            if (e.getLegajo().equals(legajo)) {
                estudiante = e;
            }
        }

        if (estudiante == null) {
            estudiante = new Estudiante(legajo, nombreEstudiante, email);
            estudiantes.add(estudiante);
        }

        solicitudes.add(new Solicitud(
                id, estudiante, tipo, prioridad, descripcion, fecha
        ));

        return id;
    }

    private void validarDatosSolicitud(String legajo, String email, TipoSolicitud tipo,
                                       PrioridadSolicitud prioridad, String fecha) {
        if (legajo == null || legajo.equals("")) {
            throw new SolicitudException("El legajo es obligatorio");
        }

        if (email == null || email.indexOf("@") == -1) {
            throw new SolicitudException("Email inválido: " + email);
        }

        if (tipo == null) {
            throw new SolicitudException("El tipo es obligatorio");
        }

        if (prioridad == null) {
            throw new SolicitudException("La prioridad es obligatoria");
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new SolicitudException("Fecha inválida: " + fecha);
        }
    }

    public void cambiarEstado(int id, EstadoSolicitud nuevoEstado) {
        for (Solicitud s : solicitudes) {
            if (s.getId() == id) {
                if (s.permiteTransicion(nuevoEstado)) {
                    s.setEstado(nuevoEstado);
                    return;
                }

                throw new SolicitudException(
                        "Transición no permitida: "
                        + s.getEstado() + " -> " + nuevoEstado
                );
            }
        }

        throw new SolicitudException("No existe la solicitud " + id);
    }

    public void asignarResponsable(int id, String nombreResponsable) {
        for (Solicitud s : solicitudes) {
            if (s.getId() == id) {
                for (Responsable r : responsables) {
                    if (r.getNombre().equals(nombreResponsable)) {
                        s.setResponsable(r);
                        return;
                    }
                }
                throw new SolicitudException("No existe el responsable " + nombreResponsable);
            }
        }
        throw new SolicitudException("No existe la solicitud " + id);
    }

    private List<Solicitud> filtrarSolicitudes(Predicate<Solicitud> criterio) {
        List<Solicitud> resultado = new ArrayList<>();

        for (Solicitud solicitud : solicitudes) {
            if (criterio.test(solicitud)) {
                resultado.add(solicitud);
            }
        }

        return resultado;
    }

    public List<Solicitud> buscarPorEstado(EstadoSolicitud estado) {
        return filtrarSolicitudes(
                solicitud -> solicitud.getEstado() == estado
        );
    }

    public List<Solicitud> buscarPorPrioridad(PrioridadSolicitud prioridad) {
        return filtrarSolicitudes(
                solicitud -> solicitud.getPrioridad() == prioridad
        );
    }

    public List<Solicitud> buscarPorTipo(TipoSolicitud tipo) {
        return filtrarSolicitudes(
                solicitud -> solicitud.getTipo() == tipo
        );
    }

    public void listarReporte() {
        int pendientes = 0, enProceso = 0, resueltas = 0, rechazadas = 0;
        System.out.println("=== REPORTE DE SOLICITUDES ===");
        for (Solicitud s : solicitudes) {
            String resp = "sin asignar";
            if (s.getResponsable() != null) {
                resp = s.getResponsable().getNombre();
            }
            System.out.println(s.getId() + " | " + s.getEstudiante().getNombre() + " | " + s.getTipo()
                    + " | " + s.getPrioridad() + " | " + s.getEstado() + " | " + resp);
            if (s.getEstado() == EstadoSolicitud.PENDIENTE) pendientes++;
            if (s.getEstado() == EstadoSolicitud.EN_PROCESO) enProceso++;
            if (s.getEstado() == EstadoSolicitud.RESUELTA) resueltas++;
            if (s.getEstado() == EstadoSolicitud.RECHAZADA) rechazadas++;
        }
        System.out.println("Pendientes: " + pendientes + " | En proceso: " + enProceso
                + " | Resueltas: " + resueltas + " | Rechazadas: " + rechazadas);
    }
}