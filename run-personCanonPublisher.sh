CONFLUENT_KAFKA_BROKER_USERNAME="$CONFLUENT_KAFKA_BROKER_USERNAME" CONFLUENT_KAFKA_BROKER_PASSWORD="$CONFLUENT_KAFKA_BROKER_PASSWORD" envsubst < ./.confluent/java.config > ./.confluent/java.config.sensitive
mvn exec:java -Dexec.mainClass="peopleStream.personCanonPublisher" -Dexec.args="./.confluent/java.config.sensitive person-canon integration-a--updates-from-canon integration-b--updates-from-canon"