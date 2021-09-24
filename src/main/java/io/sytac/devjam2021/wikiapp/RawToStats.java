package io.sytac.devjam2021.wikiapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.io.IOException;
import java.util.Properties;

public class RawToStats {
    static final String SOURCE_TOPIC = "wiki_event_raw";
    static final String SINK_TOPIC = "wiki_event_stats";

    public static void main(final String[] args) {
        final KafkaStreams streams = buildWikiStats();

        streams.cleanUp();

        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    static KafkaStreams buildWikiStats() {
        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wiki-raw-to-stats-app");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "wiki-raw-to-stats-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");

        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        final StreamsBuilder builder = new StreamsBuilder();

        // read the SOURCE_TOPIC
        final KStream<String, String> rawStream = builder.stream(
            SOURCE_TOPIC,
            Consumed.with(Serdes.String(), Serdes.String())
        );

        // calculate counts per namespace and save in KTable
        final ObjectMapper objectMapper = new ObjectMapper();
        final KTable<String, Long> eventStats = rawStream
            .mapValues(v -> {
                try {
                    final JsonNode jsonNode = objectMapper.readTree(v);
                    return jsonNode.get("namespace").asText();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .groupBy((keyIgnored, wikiNamespace) -> wikiNamespace)
            .count();

        // stream KTable to SINK_TOPIC
        eventStats.toStream().to(SINK_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

        // build topology and return app
        return new KafkaStreams(builder.build(), streamsConfiguration);
    }

}