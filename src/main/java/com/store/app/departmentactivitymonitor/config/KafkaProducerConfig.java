package com.store.app.departmentactivitymonitor.config;

import com.google.common.base.MoreObjects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("kafka.producer")
public class KafkaProducerConfig {

    private List<String> bootstrapServers;
    private String acks;
    private int batchSize;
    private String topic;
    private String avroSchemaRegistry;
    private String maxRequestSize;
    private String compressionType;
    private int retries;
    private int retryBackoffMs;
    private int maxInflightReqPerConnection;
    private String securityProtocol;
    private String saslMechanism;
    @Value("${SASL_USERNAME:abcdef}")
    private String saslUsername;
    @Value("${SASL_PASSWORD:123456}")
    private String saslPassword;

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAvroSchemaRegistry() {
        return avroSchemaRegistry;
    }

    public void setAvroSchemaRegistry(String avroSchemaRegistry) {
        this.avroSchemaRegistry = avroSchemaRegistry;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(int retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public int getMaxInflightReqPerConnection() {
        return maxInflightReqPerConnection;
    }

    public void setMaxInflightReqPerConnection(int maxInflightReqPerConnection) {
        this.maxInflightReqPerConnection = maxInflightReqPerConnection;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public String getSaslUsername() {
        return saslUsername;
    }

    public String getSaslPassword() {
        return saslPassword;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("bootstrapServers", bootstrapServers)
                          .add("acks", acks)
                          .add("batchSize", batchSize)
                          .add("topic", topic)
                          .add("avroSchemaRegistry", avroSchemaRegistry)
                          .add("maxRequestSize", maxRequestSize)
                          .add("compressionType", compressionType)
                          .add("retries", retries)
                          .add("retryBackoffMs", retryBackoffMs)
                          .add("maxInflightReqPerConnection", maxInflightReqPerConnection)
                          .add("securityProtocol", securityProtocol)
                          .add("saslMechanism", saslMechanism)
                          .add("saslUsername", saslUsername)
                          .toString();
    }
}
