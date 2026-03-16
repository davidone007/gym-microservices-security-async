package co.analisys.clases.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OcupacionClaseConsumer {

    @KafkaListener(topics = "ocupacion-clases", groupId = "monitoreo-grupo")
    public void consumirActualizacionOcupacion(String mensaje) {
        System.out.println("Dashboard actualizado en tiempo real: " + mensaje);
        // Aquí se actualizaría el dashboard (ej. WebSockets, SSE, etc.)
    }
}
