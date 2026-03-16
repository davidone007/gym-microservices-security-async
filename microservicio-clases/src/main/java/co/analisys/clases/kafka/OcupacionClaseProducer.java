package co.analisys.clases.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class OcupacionClaseProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void actualizarOcupacion(String claseId, int ocupacionActual) {
        try {
            OcupacionClase ocupacion = new OcupacionClase(claseId, ocupacionActual, LocalDateTime.now());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(ocupacion);
            kafkaTemplate.send("ocupacion-clases", claseId, json);
            System.out.println("Mensaje enviado a Kafka (Topic: ocupacion-clases): " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
