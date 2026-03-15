package co.analisys.clases.listener;

import co.analisys.clases.security.AuthContext;
import co.analisys.clases.service.ClaseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PagosListener {

    private final ClaseService claseService;
    private final RabbitTemplate rabbitTemplate;

    public PagosListener(ClaseService claseService, RabbitTemplate rabbitTemplate) {
        this.claseService = claseService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "clases-pagos-queue")
    public void onPagoAceptado(Map<String, Object> payload) {
        System.out.println("[clases] Recibido pago.aceptado: " + payload + " headerAuthPresent="
                + payload.containsKey("authToken"));

        Object miembroObj = payload.get("miembroId");
        Object claseObj = payload.get("claseId");

        if (miembroObj == null || claseObj == null) {
            System.out.println("[clases] Pago recibido sin miembroId o claseId, ignorando.");
            return;
        }

        String miembroId = String.valueOf(miembroObj);
        String claseId = String.valueOf(claseObj);

        String auth = null;
        if (payload.containsKey("authToken")) {
            Object a = payload.get("authToken");
            if (a != null)
                auth = String.valueOf(a);
        }

        try {
            AuthContext.setToken(auth);
            claseService.agregarMiembro(claseId, miembroId);

            Map<String, Object> inscripcion = new HashMap<>();
            inscripcion.put("miembroId", miembroId);
            inscripcion.put("claseId", claseId);

            rabbitTemplate.convertAndSend("inscripciones-exchange", "", inscripcion);
            System.out.println("[clases] Inscripción publicada en inscripciones-exchange: " + inscripcion);
        } catch (Exception ex) {
            System.out.println("[clases] Error al agregar miembro por pago: " + ex.getMessage());
            throw ex;
        } finally {
            AuthContext.clear();
        }
    }
}
