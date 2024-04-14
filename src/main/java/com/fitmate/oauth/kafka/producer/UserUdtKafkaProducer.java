package com.fitmate.oauth.kafka.producer;

import com.fitmate.oauth.kafka.message.UserUdtMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUdtKafkaProducer {

    private final KafkaTemplate<String, UserUdtMsg> kafkaTemplate;

    public void send(UserUdtMsg payload) {
        String topic = "user-update-event";

        log.info("sending payloa={} to topic={}", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}