package co.analisys.gimnasio.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("miembros", r -> r.path("/miembros/**")
                        .uri("http://localhost:8081"))
                .route("clases", r -> r.path("/clases/**")
                        .uri("http://localhost:8082"))
                .route("entrenadores", r -> r.path("/entrenadores/**")
                        .uri("http://localhost:8083"))
                .route("equipos", r -> r.path("/equipos/**")
                        .uri("http://localhost:8084"))
                .build();
    }
}
