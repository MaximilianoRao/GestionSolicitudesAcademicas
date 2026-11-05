public class Solicitud {
    private int id;
    private Estudiante estudiante;
    private TipoSolicitud tipo;
    private PrioridadSolicitud prioridad;
    private EstadoSolicitud estado;
    private String descripcion;
    private String fecha;
    private Responsable responsable;

    public Solicitud(int id, Estudiante estudiante, String tipo, String prioridad,
                     String descripcion, String fecha) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = TipoSolicitud.valueOf(tipo);
        this.prioridad = PrioridadSolicitud.valueOf(prioridad);
        this.estado = EstadoSolicitud.PENDIENTE;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public String getTipo() {
        return tipo.name();
    }

    public String getPrioridad() {
        return prioridad.name();
    }

    public String getEstado() {
        return estado.name();
    }

    public void setEstado(String estado) {
        this.estado = EstadoSolicitud.valueOf(estado);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }
}