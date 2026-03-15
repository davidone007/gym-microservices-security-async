package co.analisys.miembros.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Fanout exchange for inscriptions
    @Bean
    public FanoutExchange inscripcionesExchange() {
        return new FanoutExchange("inscripciones-exchange", true, false);
    }

    // Topic exchange for class schedule changes
    @Bean
    public TopicExchange clasesExchange() {
        return new TopicExchange("clases-exchange", true, false);
    }

    @Bean
    public Queue miembrosClasesQueue() {
        return QueueBuilder.durable("miembros-clases-queue").build();
    }

    @Bean
    public Binding bindingMiembrosClases(TopicExchange clasesExchange, Queue miembrosClasesQueue) {
        return BindingBuilder.bind(miembrosClasesQueue).to(clasesExchange).with("clases.horario.*");
    }

    @Bean
    public Queue miembrosInscripcionesQueue() {
        return new Queue("miembros-inscripciones-queue", true);
    }

    @Bean
    public Binding bindingInscripciones(Queue miembrosInscripcionesQueue, FanoutExchange inscripcionesExchange) {
        return BindingBuilder.bind(miembrosInscripcionesQueue).to(inscripcionesExchange);
    }
}
