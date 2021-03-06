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
public class IntegrationATransformerFrom extends PeopleStreamKafkaStream {

    final Serde<IntegrationA> integrationA = getJsonSerdeIntegrationA();
    
    IntegrationATransformerFrom() throws Exception {
      super("integration-a--retrieves", "person-canon--input", "IntegrationATransformerFrom");
    }

    public static void main(String[] args) throws Exception {
        IntegrationATransformerFrom integrationATransformerFrom = new IntegrationATransformerFrom();
        final KStream<String, IntegrationA> recordsRetrieved = integrationATransformerFrom.builder
          .stream(integrationATransformerFrom.topicIn, Consumed.with(integrationATransformerFrom.stringSerde, integrationATransformerFrom.integrationA));

        recordsRetrieved.print(Printed.<String, IntegrationA>toSysOut().withLabel("Consumed record"));
        
        KStream<String, PersonCanon> recordsTransformed = recordsRetrieved.mapValues(
          record -> new PersonCanon(String.format("%s-TRANSFORMED", record.getData()))
        );
        recordsTransformed.print(Printed.<String, PersonCanon>toSysOut().withLabel("Transformed record"));
        recordsTransformed.to(integrationATransformerFrom.topicOut, Produced.with(integrationATransformerFrom.stringSerde, integrationATransformerFrom.personCanon));

        final KafkaStreams streams = new KafkaStreams(integrationATransformerFrom.builder.build(), integrationATransformerFrom.props);
        
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

}
