package co.analisys.pagos.controller;

import co.analisys.pagos.dto.Pago;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/pagos")
public class PagosController {

    private final RabbitTemplate rabbitTemplate;

    public PagosController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<Pago> crearPago(@RequestBody Pago pago,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authorization) {
        if (pago.getId() == null)
            pago.setId(UUID.randomUUID().toString());
        if (pago.getTimestamp() == null)
            pago.setTimestamp(Instant.now());
        if (authorization != null && !authorization.isBlank()) {
            rabbitTemplate.convertAndSend("", "pagos-queue", pago, message -> {
                message.getMessageProperties().setHeader("Authorization", authorization);
                return message;
            });
        } else {
            rabbitTemplate.convertAndSend("", "pagos-queue", pago);
        }
        return ResponseEntity.accepted().body(pago);
    }
}
