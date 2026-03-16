package co.analisys.miembros.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class RecuperacionService {
    // Simulamos una base de datos para offsets
    private Map<TopicPartition, Long> offsetDb = new HashMap<>();

    public void iniciarProcesamiento() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "grupo-recuperacion");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("enable.auto.commit", "false"); // Control manual de offsets

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // Particiones especificas para tener control absoluto o suscripcion a topic
            TopicPartition tp = new TopicPartition("topic-a", 0);
            consumer.assign(Arrays.asList(tp));
            
            // Cargar último offset procesado desde una base de datos simulada
            Map<TopicPartition, Long> ultimoOffsetProcesado = cargarUltimoOffset();
            
            for (Map.Entry<TopicPartition, Long> entry : ultimoOffsetProcesado.entrySet()) {
                consumer.seek(entry.getKey(), entry.getValue());
            }
            
            System.out.println("✅ Iniciando recuperación desde offsets manuales...");
            
            // Bucle rápido para prueba
            int pollCount = 0;
            while (pollCount < 5) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    procesarRecord(record);
                    guardarOffset(new TopicPartition(record.topic(), record.partition()), record.offset() + 1);
                }
                pollCount++;
            }
        }
    }

    private Map<TopicPartition, Long> cargarUltimoOffset() {
        Map<TopicPartition, Long> offsets = new HashMap<>();
        // Offset simulado
        offsets.put(new TopicPartition("topic-a", 0), offsetDb.getOrDefault(new TopicPartition("topic-a", 0), 0L));
        return offsets;
    }

    private void procesarRecord(ConsumerRecord<String, String> record) {
        System.out.println("Procesando evento en offset: " + record.offset() + " con valor: " + record.value());
    }

    private void guardarOffset(TopicPartition tp, long offset) {
        // Guardar el offset en base de datos
        offsetDb.put(tp, offset);
        System.out.println("Offset " + offset + " guardado para " + tp);
    }
}
