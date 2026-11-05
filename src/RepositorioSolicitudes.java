import java.util.List;
import java.util.Optional;

public interface RepositorioSolicitudes {
    void agregar(Solicitud solicitud);

    Optional<Solicitud> buscarPorId(int id);

    List<Solicitud> listarTodas();
}
