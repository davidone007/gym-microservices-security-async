package co.analisys.pagos.service;

import co.analisys.pagos.dto.Pago;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagoService {

    private final RabbitTemplate rabbitTemplate;

    public PagoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "pagos-queue")
    public void procesarPago(Pago pago, org.springframework.amqp.core.Message message) {
        String auth = null;
        Object authHeader = message.getMessageProperties().getHeaders().get("Authorization");
        if (authHeader != null) {
            auth = authHeader.toString();
        }
        try {
            boolean ok = procesoPagoExitoso(pago);
            if (!ok) {
                throw new PagoFailedException("Fallo en el procesamiento del pago: " + pago.getId());
            }
            // Publicar evento asincrónico para que microservicio-clases procese la
            // inscripción
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("id", pago.getId());
            payload.put("miembroId", pago.getMiembroId());
            payload.put("claseId", pago.getClaseId());
            payload.put("amount", pago.getAmount());
            payload.put("timestamp", pago.getTimestamp());
            if (auth != null)
                payload.put("authToken", auth);

            rabbitTemplate.convertAndSend("pagos-exchange", "pago.aceptado", payload);
            System.out.println("Pago procesado y publicado: " + pago.getId() + " payload=" + payload + " headerPresent="
                    + (auth != null));
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Error en el pago, enviando a DLQ", e);
        }
    }

    private boolean procesoPagoExitoso(Pago pago) {
        // Regla simple: si amount <= 0 -> fallo
        return pago != null && pago.getAmount() > 0;
    }
}
