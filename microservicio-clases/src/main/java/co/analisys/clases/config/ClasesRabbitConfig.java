package co.analisys.clases.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class ClasesRabbitConfig {

    @Bean
    public TopicExchange pagosExchange() {
        return new TopicExchange("pagos-exchange", true, false);
    }

    @Bean
    public org.springframework.amqp.core.FanoutExchange inscripcionesExchange() {
        return new org.springframework.amqp.core.FanoutExchange("inscripciones-exchange", true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public Queue clasesPagosQueue() {
        return new Queue("clases-pagos-queue", true);
    }

    @Bean
    public Binding bindingClasesPagos(Queue clasesPagosQueue, TopicExchange pagosExchange) {
        return BindingBuilder.bind(clasesPagosQueue).to(pagosExchange).with("pago.aceptado");
    }
}
