package peopleStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import peopleStream.dataModels.IntegrationARetrieval;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.errors.TopicExistsException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class integrationARetriever {

  // Create topic in Confluent Cloud
  public static void createTopic(final String topic,
                          final Properties cloudConfig) {
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

  public static void main(final String[] args) throws IOException, InterruptedException {
    if (args.length != 2) {
      System.out.println("Please provide command line arguments: configPath topic");
      System.exit(1);
    }

    // Load properties from a local configuration file
    // Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
    // to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
    // Follow these instructions to create this file: https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java.html
    final Properties props = loadConfig(args[0]);

    // Create topic if needed
    final String topic = args[1];
    createTopic(topic, props);

    // Add additional properties.
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

    Producer<String, IntegrationARetrieval> producer = new KafkaProducer<String, IntegrationARetrieval>(props);


    System.out.printf("%n%n Produce a key and it's data to topic %s. (Set a key of 'q' to quit.) %n", topic);

    Scanner input = new Scanner(System.in);
    String userInputKey = "";
    String userInputData = "";
    while (!userInputKey.toLowerCase().equals("q")) {
        System.out.printf("Key: %n");
        userInputKey = input.next();
        if (!userInputKey.toLowerCase().equals("q")) {
          System.out.printf("Data for key: %n");
          userInputData = input.next();
          IntegrationARetrieval record = new IntegrationARetrieval(userInputData);
          System.out.printf("Producing record: %s\t%s%n", userInputKey, record);
          producer.send(new ProducerRecord<String, IntegrationARetrieval>(topic, userInputKey, record), new Callback() {
              @Override
              public void onCompletion(RecordMetadata m, Exception e) {
                if (e != null) {
                  e.printStackTrace();
                } else {
                  System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                }
              }
          });
          TimeUnit.SECONDS.sleep(1);
        }
    }

    producer.flush();

    producer.close();
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

}
