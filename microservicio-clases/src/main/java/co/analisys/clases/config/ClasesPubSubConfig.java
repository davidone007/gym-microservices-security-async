package co.analisys.clases.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClasesPubSubConfig {

    @Bean
    public TopicExchange clasesExchange() {
        return new TopicExchange("clases-exchange", true, false);
    }
}
