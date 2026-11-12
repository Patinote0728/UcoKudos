package co.edu.uco.kudos.notificacion.infraestructura;

import co.edu.uco.kudos.notificacion.aplicacion.AdaptadorNotificaciones;
import co.edu.uco.kudos.notificacion.persistencia.DispositivoPushRepositorio;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ucokudos.notificaciones.reales-habilitadas", havingValue = "true")
@ConditionalOnProperty(prefix = "ucokudos.notificaciones.firebase", name = "project-id")
public class ConfiguracionFirebase {

    @Bean(destroyMethod = "delete")
    FirebaseApp firebaseApp(@Value("${ucokudos.notificaciones.firebase.project-id}") String projectId)
            throws IOException {
        FirebaseOptions opciones = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setProjectId(projectId)
                .build();
        return FirebaseApp.initializeApp(opciones, "uco-kudos");
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    AdaptadorNotificaciones adaptadorFirebasePush(FirebaseMessaging firebaseMessaging,
                                                   DispositivoPushRepositorio dispositivos) {
        return new AdaptadorFirebasePush(firebaseMessaging, dispositivos);
    }
}
