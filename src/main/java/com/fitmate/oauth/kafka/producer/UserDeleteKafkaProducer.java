package com.fitmate.oauth.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitmate.oauth.kafka.message.UserDeleteEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserDeleteKafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public UserDeleteKafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    @Value("${spring.container-name}")
    private String containerName;
    
    @Value("${spring.kafka.topic-config.user-delete-event.topic-name}")
    private String topicName;
    public void handleEvent(UserDeleteEvent event) {
        try {
            byte[] serializedEvent = objectMapper.writeValueAsBytes(event);

            ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(
                    topicName,
                    null, // partition
                    Instant.now().toEpochMilli(), // timestamp
                    containerName, // key
                    serializedEvent
//                    List.of(new RecordHeader("containerName", containerName.getBytes())) // custom header 사용시 가능
            );

            CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(producerRecord);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("이벤트를 전송할 수 없습니다 = [{}] 원인 : {}", event, ex.getMessage());
                } else {
                    log.info("이벤트 전송 성공 = [{}] 전송된 이벤트 = [{}]", event, result.getRecordMetadata().offset());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("이벤트를 직렬화 할 수 없습니다 = [{}]: {}", event, e.getMessage());
        }
    }
}
