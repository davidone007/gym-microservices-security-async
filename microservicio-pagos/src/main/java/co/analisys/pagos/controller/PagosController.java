package co.analisys.pagos.controller;

import co.analisys.pagos.dto.Pago;
import co.analisys.pagos.service.PagoService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final PagoService pagoService;

    public PagosController(RabbitTemplate rabbitTemplate, PagoService pagoService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPago(@RequestBody Pago pago,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authorization) {
        if (pago.getId() == null)
            pago.setId(UUID.randomUUID().toString());
        if (pago.getTimestamp() == null)
            pago.setTimestamp(Instant.now());

        pagoService.registrarPago(pago);

        if (pago.getAmount() <= 0) {
            if (authorization != null && !authorization.isBlank()) {
                rabbitTemplate.convertAndSend("", "pagos-dlq", pago, message -> {
                    message.getMessageProperties().setHeader("Authorization", authorization);
                    return message;
                });
            } else {
                rabbitTemplate.convertAndSend("", "pagos-dlq", pago);
            }
            return ResponseEntity.badRequest()
                    .body("El monto debe ser mayor a 0. El mensaje fue enviado a la DLQ directamente.");
        }

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

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<?> pagosPorMiembro(@PathVariable String miembroId) {
        return ResponseEntity.ok(pagoService.listarPorMiembro(miembroId));
    }
}
