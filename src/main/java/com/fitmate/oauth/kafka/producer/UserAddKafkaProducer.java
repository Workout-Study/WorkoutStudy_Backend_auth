package com.fitmate.oauth.kafka.producer;

import com.fitmate.oauth.kafka.message.UserAddMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserAddKafkaProducer {

    private final KafkaTemplate<String, UserAddMsg> kafkaTemplate;

    public void send(UserAddMsg payload) {
        String topic = "user-create-event";

        log.info("sending payloa={} to topic={}", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}