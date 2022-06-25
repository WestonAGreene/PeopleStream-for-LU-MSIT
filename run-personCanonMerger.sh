CONFLUENT_KAFKA_BROKER_USERNAME="$CONFLUENT_KAFKA_BROKER_USERNAME" CONFLUENT_KAFKA_BROKER_PASSWORD="$CONFLUENT_KAFKA_BROKER_PASSWORD" envsubst < ./.confluent/java.config > ./.confluent/java.config.sensitive
mvn exec:java -Dexec.mainClass="integrationARetriever.personCanonMerger" -Dexec.args="./.confluent/java.config.sensitive person-canon--input person-canon person-canon-table"
