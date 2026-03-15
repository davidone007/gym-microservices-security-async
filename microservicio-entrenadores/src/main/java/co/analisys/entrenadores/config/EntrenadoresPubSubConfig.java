package co.analisys.entrenadores.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntrenadoresPubSubConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange clasesExchange() {
        return new TopicExchange("clases-exchange", true, false);
    }

    @Bean
    public Queue entrenadoresQueue() {
        return QueueBuilder.durable("entrenadores-queue").build();
    }

    @Bean
    public Binding bindingEntrenadores(TopicExchange clasesExchange, Queue entrenadoresQueue) {
        return BindingBuilder.bind(entrenadoresQueue).to(clasesExchange).with("clases.horario.*");
    }

    @Bean
    public FanoutExchange inscripcionesExchange() {
        return new FanoutExchange("inscripciones-exchange");
    }

    @Bean
    public Queue entrenadoresInscripcionesQueue() {
        return new Queue("entrenadores-inscripciones-queue", true);
    }

    @Bean
    public Binding bindingInscripciones(Queue entrenadoresInscripcionesQueue, FanoutExchange inscripcionesExchange) {
        return BindingBuilder.bind(entrenadoresInscripcionesQueue).to(inscripcionesExchange);
    }
}
