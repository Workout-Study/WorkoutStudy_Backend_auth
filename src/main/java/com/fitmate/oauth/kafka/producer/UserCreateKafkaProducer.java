package com.fitmate.oauth.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitmate.oauth.kafka.message.UserCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class UserCreateKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UserCreateKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${spring.container-name}")
    private String containerName;

    @Value("${spring.kafka.topic-config.user-create-event.topic-name}")
    private String topicName;

    public void handleEvent(UserCreateEvent event) {
        try {
//            byte[] serializedEvent = objectMapper.writeValueAsBytes(event);
//            log.info("serializedEvent: {}", serializedEvent);
            log.info("event: {}", event);
            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                    topicName,
                    null, // partition
                    Instant.now().toEpochMilli(), // timestamp
                    containerName, // key
                    event
                    // List.of(new RecordHeader("containerName", containerName.getBytes())) // custom header 사용시 가능
            );
            log.info("producerRecord = {}", producerRecord);
            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(producerRecord);
                return true;

            });
        } catch (Exception e) {
            log.info("User ID = {}", event.getUserId());
            log.error("이벤트를 전송할 수 없습니다 = [{}]: {}", event, e.getMessage());
        }
    }
}
