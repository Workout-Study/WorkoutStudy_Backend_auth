package com.fitmate.oauth.kafka.producer;

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
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(producerRecord);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("이벤트를 전송할 수 없습니다. [{}] 이유 : {}", userId, ex.getMessage());
            } else {
                log.info("메시지 [{}] 성공적으로 전송. 파티션 [{}] 오프셋 [{}]",
                        userId,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
