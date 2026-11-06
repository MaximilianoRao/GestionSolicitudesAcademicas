import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

public class GestorSolicitudes {
    private RepositorioSolicitudes repositorio;
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Responsable> responsables = new ArrayList<>();

    public GestorSolicitudes(RepositorioSolicitudes repositorio) {
        this.repositorio = repositorio;
    }

    public void agregarResponsable(String nombre, String area) {
        responsables.add(new Responsable(nombre, area));
    }

    // Registra la solicitud o lanza SolicitudException si los datos son inválidos.
    public int registrarSolicitud(int id, DatosEstudiante datosEstudiante,
                                  TipoSolicitud tipo, PrioridadSolicitud prioridad,
                                  String descripcion, String fecha) {
        if (datosEstudiante == null) {
            throw new SolicitudException("Los datos del estudiante son obligatorios");
        }

        if (repositorio.buscarPorId(id).isPresent()) {
            throw new SolicitudException("Ya existe una solicitud con id " + id);
        }
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

        repositorio.agregar(new Solicitud(
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
        Solicitud solicitud = buscarSolicitud(id);

        if (!solicitud.permiteTransicion(nuevoEstado)) {
            throw new SolicitudException(
                    "Transición no permitida: "
                    + solicitud.getEstado() + " -> " + nuevoEstado
            );
        }

        solicitud.setEstado(nuevoEstado);
    }

    public void asignarResponsable(int id, String nombreResponsable) {
        Solicitud solicitud = buscarSolicitud(id);

        for (Responsable r : responsables) {
            if (r.getNombre().equals(nombreResponsable)) {
                solicitud.setResponsable(r);
                return;
            }
        }

        throw new SolicitudException("No existe el responsable " + nombreResponsable);
    }

    public List<Solicitud> listarSolicitudes() {
        return repositorio.listarTodas();
    }

    private Solicitud buscarSolicitud(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new SolicitudException("No existe la solicitud " + id));
    }

    private List<Solicitud> filtrarSolicitudes(Predicate<Solicitud> criterio) {
        List<Solicitud> resultado = new ArrayList<>();

        for (Solicitud solicitud : repositorio.listarTodas()) {
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
}
