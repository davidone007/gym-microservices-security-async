package co.analisys.gimnasio.api_gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class MemberSummaryAggregationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<MemberSummaryAggregationGatewayFilterFactory.Config> {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public MemberSummaryAggregationGatewayFilterFactory(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String memberId = ServerWebExchangeUtils.getUriTemplateVariables(exchange).get("miembroId");
            if (memberId == null || memberId.isBlank()) {
                return writeError(exchange, HttpStatus.BAD_REQUEST, "Debe indicar el miembroId en la URL");
            }

            String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            WebClient client = webClientBuilder.build();

            Mono<JsonNode> miembroMono = getJson(client,
                    "lb://microservicio-miembros/api/miembros/{id}",
                    authorization,
                    memberId);

            Mono<ArrayNode> clasesMono = getJsonArray(client,
                    "lb://microservicio-clases/api/clases",
                    authorization)
                    .defaultIfEmpty(objectMapper.createArrayNode());

                Mono<ArrayNode> pagosMono = getJsonArray(client,
                    "lb://microservicio-pagos/pagos/miembro/{id}",
                    authorization,
                    memberId)
                    .defaultIfEmpty(objectMapper.createArrayNode());

                return Mono.zip(miembroMono, clasesMono, pagosMono)
                    .flatMap(tuple -> {
                        JsonNode miembro = tuple.getT1();
                        ArrayNode todasLasClases = tuple.getT2();
                    ArrayNode pagosDelMiembro = tuple.getT3();

                        ArrayNode clasesDelMiembro = objectMapper.createArrayNode();
                        for (JsonNode clase : todasLasClases) {
                            JsonNode miembros = clase.path("miembros");
                            if (miembros.isArray() && contieneMiembro(miembros, memberId)) {
                                clasesDelMiembro.add(clase);
                            }
                        }

                        ObjectNode resumen = objectMapper.createObjectNode();
                        resumen.set("miembro", miembro);
                        resumen.put("totalClasesInscrito", clasesDelMiembro.size());
                        resumen.set("clases", clasesDelMiembro);

                        resumen.put("totalPagos", pagosDelMiembro.size());
                        resumen.set("pagos", pagosDelMiembro);

                        return writeJson(exchange, HttpStatus.OK, resumen);
                    })
                    .onErrorResume(ex -> writeError(exchange, HttpStatus.BAD_GATEWAY,
                            "Error agregando datos de miembros/clases: " + ex.getMessage()));
        };
    }

    private Mono<JsonNode> getJson(WebClient client, String uri, String authorization, String memberId) {
        WebClient.RequestHeadersSpec<?> request = client.get().uri(uri, memberId);
        if (authorization != null && !authorization.isBlank()) {
            request = request.header(HttpHeaders.AUTHORIZATION, authorization);
        }

        return request.retrieve().bodyToMono(JsonNode.class);
    }

    private Mono<ArrayNode> getJsonArray(WebClient client, String uri, String authorization) {
        WebClient.RequestHeadersSpec<?> request = client.get().uri(uri);
        if (authorization != null && !authorization.isBlank()) {
            request = request.header(HttpHeaders.AUTHORIZATION, authorization);
        }

        return request.retrieve().bodyToMono(ArrayNode.class);
    }

    private Mono<ArrayNode> getJsonArray(WebClient client, String uri, String authorization, String memberId) {
        WebClient.RequestHeadersSpec<?> request = client.get().uri(uri, memberId);
        if (authorization != null && !authorization.isBlank()) {
            request = request.header(HttpHeaders.AUTHORIZATION, authorization);
        }

        return request.retrieve().bodyToMono(ArrayNode.class);
    }

    private boolean contieneMiembro(JsonNode miembros, String memberId) {
        for (JsonNode miembroRef : miembros) {
            if (memberId.equals(miembroRef.path("miembro_id").asText())) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("error", message);
        return writeJson(exchange, status, body);
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, JsonNode body) {
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            bytes = ("{\"error\":\"No se pudo serializar la respuesta\"}").getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    public static class Config {
    }
}
