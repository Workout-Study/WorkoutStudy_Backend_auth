package com.fitmate.oauth.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserInfoKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${spring.container-name}")
    private String containerName;

    @Value("${spring.kafka.topic-config.user-info-event.topic-name}")
    private String topicName;

    public void handleEvent(long userId) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                topicName,
                userId
        );

    }
}
