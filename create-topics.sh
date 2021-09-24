docker-compose run kafka /opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 \
  --topic wiki_event_raw --partitions 1 --replication-factor 1
docker-compose run kafka /opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 \
  --topic wiki_event_stats --partitions 1 --replication-factor 1

/opt/bitnami/kafka/bin/kafka-topics.sh --describe --zookeeper zookeeper:2181