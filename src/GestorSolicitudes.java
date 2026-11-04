import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeParseException;

public class GestorSolicitudes {
    private List<Solicitud> solicitudes = new ArrayList<>();
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Responsable> responsables = new ArrayList<>();

    public void agregarResponsable(String nombre, String area) {
        responsables.add(new Responsable(nombre, area));
    }

    // Retorna el id de la solicitud registrada; lanza SolicitudException si los datos son inválidos
    public int registrarSolicitud(int id, String legajo, String nombreEstudiante, String email,
                                  String tipo, String prioridad, String descripcion, String fecha) {
        if (legajo == null || legajo.equals("")) {
            throw new SolicitudException("El legajo es obligatorio");
        }
        if (email == null || email.indexOf("@") == -1) {
            throw new SolicitudException("Email inválido: " + email);
        }
        if (!(tipo.equals("CONSTANCIA") || tipo.equals("EQUIVALENCIA") || tipo.equals("BECA") || tipo.equals("OTRO"))) {
            throw new SolicitudException("Tipo inválido: " + tipo);
        }
        if (!(prioridad.equals("ALTA") || prioridad.equals("MEDIA") || prioridad.equals("BAJA"))) {
            throw new SolicitudException("Prioridad inválida: " + prioridad);
        }
        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new SolicitudException("Fecha inválida: " + fecha);
        }
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
        solicitudes.add(new Solicitud(id, estudiante, tipo, prioridad, descripcion, fecha));
        return id;
    }

    public void cambiarEstado(int id, String nuevoEstado) {
        for (Solicitud s : solicitudes) {
            if (s.getId() == id) {
                if (s.getEstado().equals("PENDIENTE")) {
                    if (nuevoEstado.equals("EN_PROCESO")) {
                        s.setEstado(nuevoEstado);
                        return;
                    }
                } else if (s.getEstado().equals("EN_PROCESO")) {
                    if (nuevoEstado.equals("RESUELTA") || nuevoEstado.equals("RECHAZADA")) {
                        s.setEstado(nuevoEstado);
                        return;
                    }
                }
                throw new SolicitudException("Transición no permitida: " + s.getEstado() + " -> " + nuevoEstado);
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

    public List<Solicitud> buscarPorEstado(String estado) {
        List<Solicitud> resultado = new ArrayList<>();
        for (Solicitud s : solicitudes) {
            if (s.getEstado().equals(estado)) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    public List<Solicitud> buscarPorPrioridad(String prioridad) {
        List<Solicitud> resultado = new ArrayList<>();
        for (Solicitud s : solicitudes) {
            if (s.getPrioridad().equals(prioridad)) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    public List<Solicitud> buscarPorTipo(String tipo) {
        List<Solicitud> resultado = new ArrayList<>();
        for (Solicitud s : solicitudes) {
            if (s.getTipo().equals(tipo)) {
                resultado.add(s);
            }
        }
        return resultado;
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
            if (s.getEstado().equals("PENDIENTE")) pendientes++;
            if (s.getEstado().equals("EN_PROCESO")) enProceso++;
            if (s.getEstado().equals("RESUELTA")) resueltas++;
            if (s.getEstado().equals("RECHAZADA")) rechazadas++;
        }
        System.out.println("Pendientes: " + pendientes + " | En proceso: " + enProceso
                + " | Resueltas: " + resueltas + " | Rechazadas: " + rechazadas);
    }
}