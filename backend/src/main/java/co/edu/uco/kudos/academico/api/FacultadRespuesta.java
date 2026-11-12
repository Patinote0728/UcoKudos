package co.edu.uco.kudos.academico.api;

import co.edu.uco.kudos.academico.dominio.Facultad;
import java.util.UUID;

public record FacultadRespuesta(UUID id, String nombre) {
    public static FacultadRespuesta desde(Facultad facultad) {
        return new FacultadRespuesta(facultad.getId(), facultad.getNombre());
    }
}
