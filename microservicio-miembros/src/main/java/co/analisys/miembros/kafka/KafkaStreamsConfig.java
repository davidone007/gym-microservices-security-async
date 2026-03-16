package co.analisys.miembros.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean
    public KStream<String, DatosEntrenamiento> kStream(StreamsBuilder streamsBuilder) {
        JsonSerde<DatosEntrenamiento> datosSerde = new JsonSerde<>(DatosEntrenamiento.class);
        JsonSerde<ResumenEntrenamiento> resumenSerde = new JsonSerde<>(ResumenEntrenamiento.class);

        KStream<String, DatosEntrenamiento> stream = streamsBuilder.stream("datos-entrenamiento", Consumed.with(Serdes.String(), datosSerde));

        stream.groupByKey()
              .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(7)))
              .aggregate(
                  ResumenEntrenamiento::new,
                  (key, value, aggregate) -> aggregate.actualizar(value),
                  Materialized.with(Serdes.String(), resumenSerde)
              )
              .toStream()
              .map((key, value) -> new org.apache.kafka.streams.KeyValue<>(key.key(), value))
              .to("resumen-entrenamiento", Produced.with(Serdes.String(), resumenSerde));

        return stream;
    }
}
