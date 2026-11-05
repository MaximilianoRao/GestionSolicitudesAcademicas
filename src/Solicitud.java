public class Solicitud {
    private int id;
    private Estudiante estudiante;
    private TipoSolicitud tipo;
    private PrioridadSolicitud prioridad;
    private EstadoSolicitud estado;
    private String descripcion;
    private String fecha;
    private Responsable responsable;

    public Solicitud(int id, Estudiante estudiante, TipoSolicitud tipo, PrioridadSolicitud prioridad,
                     String descripcion, String fecha) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
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

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public PrioridadSolicitud getPrioridad() {
        return prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public boolean permiteTransicion(EstadoSolicitud nuevoEstado) {
        return estado.permiteTransicion(nuevoEstado);
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
