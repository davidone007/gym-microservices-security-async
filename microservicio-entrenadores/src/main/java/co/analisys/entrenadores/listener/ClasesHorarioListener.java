package co.analisys.entrenadores.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ClasesHorarioListener {

    @RabbitListener(queues = "entrenadores-queue")
    public void onHorarioActualizado(Map<String, Object> payload) {
        System.out.println("[entrenadores] Notificación de cambio de horario recibida: " + payload);
        // Aquí se notificaría al entrenador sobre el cambio de horario de su clase
        String claseId = String.valueOf(payload.get("claseId"));
        List<Integer> horario = (List<Integer>) payload.get("nuevoHorario");

        String horarioFormateado = horario.get(0) + "-" +
                String.format("%02d", horario.get(1)) + "-" +
                String.format("%02d", horario.get(2)) + " " +
                String.format("%02d", horario.get(3)) + ":" +
                String.format("%02d", horario.get(4));
        Object entrenadorId = payload.get("entrenadorId");

        if (entrenadorId != null) {
            System.out.println("[entrenadores] Notificando al entrenador " + entrenadorId
                    + " cambio de horario de clase " + claseId + " a " + horarioFormateado);
        } else {
            System.out.println(
                    "[entrenadores] Cambio de horario de clase " + claseId + " anotado (sin entrenador asignado).");
        }
    }
}
