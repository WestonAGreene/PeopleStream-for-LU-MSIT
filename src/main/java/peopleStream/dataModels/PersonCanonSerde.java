package peopleStream.dataModels;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class PersonCanonSerde implements Serde<PersonCanon> {
    private PersonCanonSerializer serializer = new PersonCanonSerializer();
    private PersonCanonDeserializer deserializer = new PersonCanonDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<PersonCanon> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<PersonCanon> deserializer() {
        return deserializer;
    }
}