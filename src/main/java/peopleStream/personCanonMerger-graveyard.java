// I was unable to query the historical record of the table within the kafkastream.
// Without the historical record, I was unable to do a merge.
// A copy of my failed attempts is the personCanonMerger-graveyard.java


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

import peopleStream.dataModels.PersonCanon;
import peopleStream.dataModels.PersonCanonSerde;

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

import org.apache.kafka.streams.kstream.KTable;

import io.confluent.common.utils.TestUtils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.ForeachAction;

public class personCanonMerger {

    public static void main(String[] args) throws Exception {

        // Consume Events
        // Determine if novel
        // if novel, merge with person table
        // Update person table
        // Produce to person-canon

        if (args.length != 5) {
          System.out.println("Please provide command line arguments: configPath topicIn topicOut topicTableIn topicTableOut");
          System.exit(1);
        }

        final Serde<PersonCanon> PersonCanon = getJsonSerdePersonCanon();
        final Serde<String> stringSerde = Serdes.String();

        final Properties propsForTopics = loadConfig(args[0]);
        final String topicIn = args[1];
        createTopic(topicIn, propsForTopics);
        final String topicOut = args[2];
        createTopic(topicOut, propsForTopics);
        final String topicTableIn = args[3];
        createTopic(topicTableIn, propsForTopics);
        final String topicTableOut = args[4];
        createTopic(topicTableOut, propsForTopics);
        propsForTopics.put(StreamsConfig.APPLICATION_ID_CONFIG, "personCanonMerger");
        propsForTopics.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        propsForTopics.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Properties propsForTable = loadConfig(args[0]);
        propsForTable.put(StreamsConfig.APPLICATION_ID_CONFIG, "personCanonMergerTable");
        propsForTable.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        propsForTable.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        propsForTable.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        propsForTable.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, PersonCanonSerde.class);

        final StreamsBuilder builderForTables = new StreamsBuilder();
        final KStream<String, PersonCanon> streamsForTableCreation = builderForTables.stream(topicTableIn, Consumed.with(stringSerde, PersonCanon));
        final KTable<String, PersonCanon> convertedToTable = streamsForTableCreation.toTable(Materialized.as(topicTableOut));
        streamsForTableCreation.to(topicOut, Produced.with(stringSerde, PersonCanon));
        convertedToTable.toStream().to(topicTableOut, Produced.with(stringSerde, PersonCanon));

        final KafkaStreams streamFromTable = new KafkaStreams(builderForTables.build(), propsForTable);
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler to catch Control-C.
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streamFromTable.close(Duration.ofSeconds(5));
                latch.countDown();
            }
        });

        try {
            streamFromTable.cleanUp();
            streamFromTable.start();            
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }


        System.exit(0);

        // // final personCanonMerger instance = new personCanonMerger();
        // final StreamsBuilder builderForTopics = new StreamsBuilder();

        // final Serde<PersonCanon> PersonCanon = getJsonSerdePersonCanon();
        // final Serde<String> stringSerde = Serdes.String();

        // final KStream<String, PersonCanon> streamForTopics = builderForTopics.stream(topicIn, Consumed.with(stringSerde, PersonCanon));
        // streamForTopics.print(Printed.<String, PersonCanon>toSysOut().withLabel("Consumed record"));


        // streamForTopics
        //     .toTable(Materialized.as(topicTableOut))
        //     .toStream()
        //     .to(topicTableOut, Produced.with(stringSerde, PersonCanon))
        // ;


        // // final KStream<String, PersonCanon> streamWMarkDiffValueFromTable = stream.foreach(new ForeachAction<String, PersonCanon>() {
        // //     public KeyValue<String, PersonCanon> apply(String key, PersonCanon value) {
        // //         ReadOnlyKeyValueStore<String, PersonCanon> personCanonTable =
        // //             streams.store(StoreQueryParameters.fromNameAndType(topicTableOut, QueryableStoreTypes.keyValueStore()));
        // //         Boolean diffValueFromTable = personCanonTable.get(key).getData().equals(person.getData());
        // //         return new KeyValue<String, PersonCanon>(key, new PersonCanon(value.getData(), diffValueFromTable));
        // //     }
        // // });
        // // final KTable<String, PersonCanon> convertedTable = stream

        // final KafkaStreams streamsForTable = new KafkaStreams(builderForTables.build(), propsForTable);
        // // streamsForTable.cleanUp();
        // streamsForTable.start();
        // ReadOnlyKeyValueStore<String, PersonCanon> personCanonTable =
        //     streamsForTable.store(StoreQueryParameters.fromNameAndType(topicTableOut, QueryableStoreTypes.keyValueStore()));

        // streamForTopics
        //     .map((key, value) -> {
        //         Boolean diffValueFromTable = personCanonTable.get(key).getData().equals(value.getData());
        //         return new KeyValue<String, PersonCanon>(key, new PersonCanon(value.getData(), diffValueFromTable));
        //     })
        //     .filter((k, v) -> v.getDiffValueFromTable())
        //     .to(topicOut, Produced.with(stringSerde, PersonCanon))
        // ;

        // final KStream<String, PersonCanon> streamsForTableFromOutTopic = builderForTopics.stream(topicOut, Consumed.with(stringSerde, PersonCanon));
        // streamsForTableFromOutTopic.print(Printed.<String, PersonCanon>toSysOut().withLabel("Consumed record post-filter"));

        // // resource: https://www.michael-noll.com/blog/2018/04/05/of-stream-and-tables-in-kafka-and-stream-processing-part1/
        // // KStream<String, String> stream = ...;
        // // KTable<String, String> table = stream
        // //                         .groupByKey()
        // //                         .reduce((aggV, newV) -> newV);        
        // streamsForTableFromOutTopic
        //     .toTable(Materialized.as(topicTableOut))
        //     .toStream()
        //     .to(topicTableOut, Produced.with(stringSerde, PersonCanon))
        // ;
        
        // // streamWMarkDiffValueFromTable.print(Printed.<String, PersonCanon>toSysOut().withLabel("Consumed record"));

        // // streamWMarkDiffValueFromTable
        // //     .filter((k, v) -> v.getDiffValueFromTable())
        // //     .to(topicOut, Produced.with(stringSerde, PersonCanon));

        // // final KTable<String, PersonCanon> convertedTable = streamWMarkDiffValueFromTable.toTable(Materialized.as("stream-converted-to-table"));
        // // convertedTable
        // //     .toStream()
        // //     .filter((k, v) -> v.getDiffValueFromTable())
        // //     .to(topicTableOut, Produced.with(stringSerde, PersonCanon));

        // final KafkaStreams streamsToProduce = new KafkaStreams(builderForTopics.build(), propsForTopics);
        // final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler to catch Control-C.
        // Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
        //     @Override
        //     public void run() {
        //         streamsToProduce.close(Duration.ofSeconds(5));
        //         streamsForTable.close(Duration.ofSeconds(5));
        //         latch.countDown();
        //     }
        // });

        // try {
        //     streamsToProduce.cleanUp();
        //     streamsToProduce.start();
        //     latch.await();
        // } catch (Throwable e) {
        //     System.exit(1);
        // }
        // System.exit(0);

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
