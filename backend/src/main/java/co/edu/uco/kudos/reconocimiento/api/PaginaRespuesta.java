package co.edu.uco.kudos.reconocimiento.api;

import java.util.List;

public record PaginaRespuesta<T>(
        List<T> elementos,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas
) {
}
