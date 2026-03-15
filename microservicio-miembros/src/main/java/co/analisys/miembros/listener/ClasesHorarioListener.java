package co.analisys.miembros.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;

@Component
public class ClasesHorarioListener {

    @RabbitListener(queues = "miembros-clases-queue")
    public void onHorarioActualizado(Map<String, Object> payload) {
        System.out.println("[miembros] Notificación de cambio de horario recibida: " + payload);

        List<Integer> horario = (List<Integer>) payload.get("nuevoHorario");

        String horarioFormateado = horario.get(0) + "-" +
                String.format("%02d", horario.get(1)) + "-" +
                String.format("%02d", horario.get(2)) + " " +
                String.format("%02d", horario.get(3)) + ":" +
                String.format("%02d", horario.get(4));
        // Aquí se notificaría a los miembros afectados por el cambio de horario
        String claseId = String.valueOf(payload.get("claseId"));
        System.out.println("[miembros] Simulando notificación masiva de cambio de horario para la clase " + claseId
                + " a " + horarioFormateado);
    }
}
