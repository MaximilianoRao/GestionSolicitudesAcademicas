public class Solicitud {
    private int id;
    private Estudiante estudiante;
    private String tipo;
    private String prioridad;
    private String estado;
    private String descripcion;
    private String fecha;
    private Responsable responsable;

    public Solicitud(int id, Estudiante estudiante, String tipo, String prioridad,
                     String descripcion, String fecha) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = "PENDIENTE";
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public String getTipo() { return tipo; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }
    public Responsable getResponsable() { return responsable; }
    public void setResponsable(Responsable responsable) { this.responsable = responsable; }
}