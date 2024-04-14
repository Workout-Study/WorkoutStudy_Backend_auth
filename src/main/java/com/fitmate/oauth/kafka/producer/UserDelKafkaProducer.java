package com.fitmate.oauth.kafka.producer;

import com.fitmate.oauth.kafka.message.UserDelMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDelKafkaProducer {

    private final KafkaTemplate<String, UserDelMsg> kafkaTemplate;

    public void send(UserDelMsg payload) {
        String topic = "user-delete-event";

        log.info("sending payloa={} to topic={}", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}