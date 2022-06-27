# PeopleStream-for-LU-MSIT
Final project for CSIS505 for a MSIT at Liberty University

# RUN
Set credentials for accessing the Kafka Cluster hosted by Confluent:
    ```
        export CONFLUENT_KAFKA_BROKER_USERNAME=redact
        export CONFLUENT_KAFKA_BROKER_PASSWORD=redact
    ```

Run in separate terminals all `run-*.sh` files. For example:
    ```
        ./run-personCanonPublisher.sh
        ./run-personCanonMerger.sh
        ./run-integrationBUpdater.sh
        ./run-integrationBTransformerTo.sh
        ./run-integrationBTransformerFrom.sh
        ./run-integrationBRetriever.sh
        ./run-integrationAUpdater.sh
        ./run-integrationATransformerTo.sh
        ./run-integrationATransformerFrom.sh
        ./run-integrationARetriever.sh
    ```
Then type key and value data into the terminals of the `run-integration*Retriever.sh` terminals
and watch for output in the `./run-integration*Updater.sh` terminals.