package peopleStream;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.errors.TopicExistsException;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.KeyValueStore;

import peopleStream.dataModels.IntegrationA;
import peopleStream.dataModels.PersonCanon;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;

import java.util.Collections;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class integrationATransformerTo {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
          System.out.println("Please provide command line arguments: configPath topicIn topicOut");
          System.exit(1);
        }
    
        final Properties props = loadConfig(args[0]);

        final String topicIn = args[1];
        createTopic(topicIn, props);
        final String topicOut = args[2];
        createTopic(topicOut, props);
    
        // Load properties from a local configuration file
        // Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        // to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        // Follow these instructions to create this file: https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java.html

        // Add additional properties.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "integrationATransformerTo");
        // Disable caching to print the aggregation value after each record
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Serde<PersonCanon> PersonCanon = getJsonSerdePersonCanon();
        final Serde<IntegrationA> IntegrationA = getJsonSerdeIntegrationA();

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, PersonCanon> recordsRetrieved = builder.stream(topicIn, Consumed.with(Serdes.String(), PersonCanon));

        recordsRetrieved.print(Printed.<String, PersonCanon>toSysOut().withLabel("Consumed record"));
        

        KStream<String, IntegrationA> recordsTransformed = recordsRetrieved.mapValues(
          record -> new IntegrationA(String.format("%s-TRANSFORMED-for-A", record.getData()))
        );
        recordsTransformed.print(Printed.<String, IntegrationA>toSysOut().withLabel("Transformed record"));
        recordsTransformed.to(topicOut, Produced.with(Serdes.String(), IntegrationA));

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }

    private static Serde<IntegrationA> getJsonSerdeIntegrationA(){

        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("json.value.type", IntegrationA.class);

        final Serializer<IntegrationA> mySerializer = new KafkaJsonSerializer<>();
        mySerializer.configure(serdeProps, false);

        final Deserializer<IntegrationA> myDeserializer = new KafkaJsonDeserializer<>();
        myDeserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(mySerializer, myDeserializer);
    }

    private static Serde<PersonCanon> getJsonSerdePersonCanon(){

        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("json.value.type", PersonCanon.class);

        final Serializer<PersonCanon> mySerializer = new KafkaJsonSerializer<>();
        mySerializer.configure(serdeProps, false);

        final Deserializer<PersonCanon> myDeserializer = new KafkaJsonDeserializer<>();
        myDeserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(mySerializer, myDeserializer);
    }

    public static Properties loadConfig(final String configFile) throws IOException {
      if (!Files.exists(Paths.get(configFile))) {
        throw new IOException(configFile + " not found.");
      }
      final Properties cfg = new Properties();
      try (InputStream inputStream = new FileInputStream(configFile)) {
        cfg.load(inputStream);
      }
      return cfg;
    }

    public static void createTopic(final String topic, final Properties cloudConfig) {
        final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
        try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

}
