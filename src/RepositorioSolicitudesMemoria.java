import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioSolicitudesMemoria implements RepositorioSolicitudes {
    private List<Solicitud> solicitudes = new ArrayList<>();

    @Override
    public void agregar(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == id) {
                return Optional.of(solicitud);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Solicitud> listarTodas() {
        return new ArrayList<>(solicitudes);
    }
}
