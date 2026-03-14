package co.analisys.clases;

import co.analisys.clases.config.JwtPropagationInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class ClasesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClasesApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(JwtPropagationInterceptor jwtPropagationInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(jwtPropagationInterceptor));
        return restTemplate;
    }
}
