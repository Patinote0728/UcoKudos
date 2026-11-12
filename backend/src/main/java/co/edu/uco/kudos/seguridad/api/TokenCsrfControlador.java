package co.edu.uco.kudos.seguridad.api;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenCsrfControlador {

    @GetMapping("/api/csrf")
    public TokenCsrfRespuesta obtenerToken(CsrfToken token) {
        return new TokenCsrfRespuesta(token.getHeaderName(), token.getToken());
    }
}
