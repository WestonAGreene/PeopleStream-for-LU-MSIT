CONFLUENT_KAFKA_BROKER_USERNAME="$CONFLUENT_KAFKA_BROKER_USERNAME" CONFLUENT_KAFKA_BROKER_PASSWORD="$CONFLUENT_KAFKA_BROKER_PASSWORD" envsubst < ./.confluent/java.config > ./.confluent/java.config.sensitive
mvn exec:java -Dexec.mainClass="peopleStream.integrationBTransformerFrom" -Dexec.args="./.confluent/java.config.sensitive integration-b--retrieves person-canon--input"
