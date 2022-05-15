package com.store.app.departmentactivitymonitor.streaming;

import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class AvroProducer {

    private final Producer<String, Object> producer;
    private final String topic;

    public String getTopic() {
        return topic;
    }

    AvroProducer(Producer<String, Object> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public Future<RecordMetadata> send(String key, Object value) {
        return send(key, value, null);
    }

    public Future<RecordMetadata> send(String key, Object value, Callback callback) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, value);
        if (callback == null) {
            return producer.send(record);
        }
        return producer.send(record, callback);
    }
}
