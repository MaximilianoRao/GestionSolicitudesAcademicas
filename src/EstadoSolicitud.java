public enum EstadoSolicitud {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA,
    RECHAZADA;

    public boolean permiteTransicion(String nuevoEstado) {
        if (this == PENDIENTE) {
            return nuevoEstado.equals("EN_PROCESO");
        }

        if (this == EN_PROCESO) {
            return nuevoEstado.equals("RESUELTA")
                    || nuevoEstado.equals("RECHAZADA");
        }

        return false;
    }
}