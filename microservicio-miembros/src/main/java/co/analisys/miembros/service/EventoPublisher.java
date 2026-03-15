package co.analisys.miembros.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EventoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarInscripcion(Map<String, Object> payload) {
        rabbitTemplate.convertAndSend("inscripciones-exchange", "", payload);
    }

    public void publicarCambioHorario(String routingKey, Map<String, Object> payload) {
        rabbitTemplate.convertAndSend("clases-exchange", routingKey, payload);
    }
}
