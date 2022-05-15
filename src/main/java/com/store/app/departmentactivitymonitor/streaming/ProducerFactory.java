package com.store.app.departmentactivitymonitor.streaming;

import com.store.app.departmentactivitymonitor.config.KafkaProducerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ProducerFactory {

    private final KafkaProducerConfig config;
    private static final String JAAS_TEMPLATE = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";

    @Autowired
    public ProducerFactory(KafkaProducerConfig config) {
        this.config = config;
    }

    public AvroProducer createAvroProducer() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", config.getBootstrapServers()));
        p.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
        p.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getBatchSize());
        p.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.getAvroSchemaRegistry());
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        p.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, config.getMaxRequestSize());
        p.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, config.getCompressionType());
        p.put(ProducerConfig.RETRIES_CONFIG, config.getRetries());
        p.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, config.getRetryBackoffMs());
        p.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, config.getMaxInflightReqPerConnection());

        // SASL config
        p.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, config.getSecurityProtocol());
        p.put(SaslConfigs.SASL_MECHANISM, config.getSaslMechanism());
        p.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(JAAS_TEMPLATE, config.getSaslUsername(), config.getSaslPassword()));
        p.put(KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "SASL_INHERIT");

        return new AvroProducer(new KafkaProducer<>(p), config.getTopic());
    }
}
