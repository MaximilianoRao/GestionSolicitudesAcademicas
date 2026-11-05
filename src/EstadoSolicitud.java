public enum EstadoSolicitud {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA,
    RECHAZADA;

    public boolean permiteTransicion(EstadoSolicitud nuevoEstado) {
        if (this == PENDIENTE) {
            return nuevoEstado == EN_PROCESO;
        }

        if (this == EN_PROCESO) {
            return nuevoEstado == RESUELTA || nuevoEstado == RECHAZADA;
        }

        return false;
    }
}
