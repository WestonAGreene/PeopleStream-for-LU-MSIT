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

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.ForeachAction;
public class integrationATransformerFrom {

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
        // final String topicTableIn = "person-canon-table";
        // createTopic(topicTableIn, props);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "integrationATransformerFrom");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Serde<IntegrationA> IntegrationA = getJsonSerdeIntegrationA();
        final Serde<PersonCanon> PersonCanon = getJsonSerdePersonCanon();
        final Serde<String> stringSerde = Serdes.String();

        // final StreamsBuilder builderForTable = new StreamsBuilder();
        // final KStream<String, PersonCanon> personCanonTableBuild = builderForTable.stream(topicTableIn, Consumed.with(stringSerde, PersonCanon));
        // // KGroupedStream<String, PersonCanon> groupedByKey = personCanonTableBuild
        // //   .selectKey((key, value) -> key);
        // KGroupedStream<String, PersonCanon> groupedByKey = personCanonTableBuild.groupByKey();
        // // KGroupedStream<String, PersonCanon> groupedByKey = personCanonTableBuild
        // //   .groupBy((key, value) -> key, Grouped.with(stringSerde, PersonCanon));
        // KafkaStreams personaCanonTableStream = new KafkaStreams(builderForTable.build(), props);
        // personaCanonTableStream.start();
        // ReadOnlyKeyValueStore<String, PersonCanon> personaCanonTable =
        //     personaCanonTableStream.store("person-canon-table", QueryableStoreTypes.<String, PersonCanon>keyValueStore());
        // // ReadOnlyKeyValueStore<String, String> personaCanonTable =
        // //     personaCanonTableStream.store("person-canon-table", QueryableStoreTypes.<String, String>keyValueStore());
        //     // personaCanonTableStream.store(StoreQueryParameters.fromNameAndType("person-canon-table-query", QueryableStoreTypes.keyValueStore()));
        //     // personaCanonTableStream.store("person-canon-table", QueryableStoreTypes.<String, String>keyValueStore());
        //     // personaCanonTableStream.store("person-canon-table", QueryableStoreTypes.<KeyValue<String, PersonCanon>>keyValueStore());
        //     // personaCanonTableStream.store("person-canon-table", QueryableStoreTypes.<String>keyValueStore());
        //     // personaCanonTableStream.store("person-canon-table", QueryableStoreTypes.<String, PersonCanon>keyValueStore());
        //     // personaCanonTableStream.<String, PersonCanon>store("person-canon-table", QueryableStoreTypes.<String, PersonCanon>keyValueStore());
        //     // personaCanonTableStream.store(StoreQueryParameters.fromNameAndType("person-canon-table", QueryableStoreTypes.keyValueStore()));
        
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, IntegrationA> recordsRetrieved = builder.stream(topicIn, Consumed.with(stringSerde, IntegrationA));

        recordsRetrieved.print(Printed.<String, IntegrationA>toSysOut().withLabel("Consumed record"));
        
        KStream<String, PersonCanon> recordsTransformed = recordsRetrieved.mapValues(
          record -> new PersonCanon(String.format("%s-TRANSFORMED", record.getData()))
        );
        recordsTransformed.print(Printed.<String, PersonCanon>toSysOut().withLabel("Transformed record"));
        // recordsTransformed.foreach(new ForeachAction<String, PersonCanon>() {
        //     public void apply(String key, PersonCanon value) {
        //         System.out.println("Key from table:" + key + " | Value: not yet " + value.toString());
        //     }
        // });
        recordsTransformed.to(topicOut, Produced.with(stringSerde, PersonCanon));

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
