package co.analisys.miembros.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InscripcionListener {

    @RabbitListener(queues = "miembros-inscripciones-queue")
    public void onInscripcion(Map<String, Object> inscripcion) {
        System.out.println("[miembros] Notificación de inscripción recibida: " + inscripcion);
        // Aquí podrías, por ejemplo, enviar un email de confirmación al miembro
        String miembroId = String.valueOf(inscripcion.get("miembroId"));
        String claseId = String.valueOf(inscripcion.get("claseId"));
        System.out.println("[miembros] Enviando email de confirmación a " + miembroId + " para la clase " + claseId);
    }
}
