package co.analisys.clases.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import co.analisys.clases.security.AuthContext;

import java.io.IOException;

/**
 * Interceptor que propaga el header Authorization (JWT) de la petición entrante
 * a las llamadas salientes con RestTemplate (p. ej. a entrenadores, equipos,
 * miembros).
 */
@Component
public class JwtPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest servletRequest = attrs.getRequest();
            String authorization = servletRequest.getHeader("Authorization");
            if (authorization != null && !authorization.isBlank()) {
                request.getHeaders().set("Authorization", authorization);
            }
        } else {
            String auth = AuthContext.getToken();
            if (auth != null && !auth.isBlank()) {
                request.getHeaders().set("Authorization", auth);
            }
        }
        return execution.execute(request, body);
    }
}
