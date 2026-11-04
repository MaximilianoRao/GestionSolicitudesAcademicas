public class EvidenciaEtapa2 {
    public static void main(String[] args) {
        GestorSolicitudes gestor = new GestorSolicitudes();
        gestor.agregarResponsable("Laura Gomez", "Alumnado");
        System.out.println("Fecha invalida: " + gestor.registrarSolicitud(1, "L001", "Ana Lopez", "ana@mail.com", "BECA", "ALTA", "Consulta", "abc"));
        System.out.println("Asignar a solicitud inexistente: " + gestor.asignarResponsable(99, "Laura Gomez"));
        System.out.println("Asignar a responsable inexistente: " + gestor.asignarResponsable(1, "Nadie"));
        gestor.listarReporte();
    }
}
