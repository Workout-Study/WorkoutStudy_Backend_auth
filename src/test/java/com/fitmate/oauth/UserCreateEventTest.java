package com.fitmate.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitmate.oauth.kafka.message.UserCreateEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCreateEventTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSerialization() throws JsonProcessingException {
        UserCreateEvent event = new UserCreateEvent(1L, "nickname", true, "createdAt", "updatedAt");
        String serialized = objectMapper.writeValueAsString(event);
        System.out.println(serialized);

        UserCreateEvent deserialized = objectMapper.readValue(serialized, UserCreateEvent.class);
        assertEquals(event.getUserId(), deserialized.getUserId());
        assertEquals(event.getNickname(), deserialized.getNickname());
        assertEquals(event.getState(), deserialized.getState());
        assertEquals(event.getCreatedAt(), deserialized.getCreatedAt());
        assertEquals(event.getUpdatedAt(), deserialized.getUpdatedAt());
    }
}

