package co.edu.uco.kudos.usuario.infraestructura;

import co.edu.uco.kudos.usuario.aplicacion.EntregaCodigoVerificacion;
import co.edu.uco.kudos.usuario.dominio.CanalVerificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class EntregaCodigoLocal implements EntregaCodigoVerificacion {
    private static final Logger LOG = LoggerFactory.getLogger(EntregaCodigoLocal.class);
    @Override public void enviar(CanalVerificacion canal, String destino, String codigo) {
        LOG.info("Código local de verificación para {} ({}): {}", destino, canal, codigo);
    }
}
