package co.analisys.entrenadores.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InscripcionListener {

    @RabbitListener(queues = "entrenadores-inscripciones-queue")
    public void onInscripcion(Map<String, Object> inscripcion) {
        System.out.println("[entrenadores] Notificación de inscripción recibida: " + inscripcion);
        // Aquí podrías notificar al entrenador que un nuevo miembro se ha unido a su
        // clase
        String claseId = String.valueOf(inscripcion.get("claseId"));
        System.out.println(
                "[entrenadores] Notificando al entrenador de la clase " + claseId + " sobre el nuevo miembro.");
    }
}
