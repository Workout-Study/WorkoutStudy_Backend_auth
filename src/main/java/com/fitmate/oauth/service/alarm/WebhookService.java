package com.fitmate.oauth.service.alarm;

import com.fitmate.oauth.dto.alarmDto.ChatDto;
import com.fitmate.oauth.dto.alarmDto.PenaltyCompleteDto;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WebhookService {

    private final UsersRepository usersRepository;
    private final FitGroupService fitGroupService;

    public void sendPenaltyCompleteWebhook(PenaltyCompleteDto penaltyCompleteDto) {
        Users users = usersRepository.findByUserId(penaltyCompleteDto.getUserId());
        String token = users.getFcmToken();
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(String.valueOf(penaltyCompleteDto.getUserId()))
                        .setBody(penaltyCompleteDto.getMessage())
                        .build())
                .putData("userId", String.valueOf(penaltyCompleteDto.getUserId()))
                .putData("message", penaltyCompleteDto.getMessage())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setTitle(String.valueOf(penaltyCompleteDto.getUserId()))
                                .setBody(penaltyCompleteDto.getMessage())
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(42)
                                .build())
                        .build())
                .setTopic("wallet-service")
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM send-" + response);
        } catch (FirebaseMessagingException e) {
            log.info("FCM except-" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendRealTimeChatWebhook(ChatDto chatDto) {
        Long fitGroupId = (long) chatDto.getFitgroupId();
        List<Long> fitMateIdsFromExternalApi = fitGroupService.getFitMateIdsFromExternalApi(fitGroupId, chatDto.getUserId());

        for (Long userId : fitMateIdsFromExternalApi) {
            Users users = usersRepository.findByUserId(userId);
            String token = users.getFcmToken();
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(String.valueOf(chatDto.getUserId()))
                            .setBody(chatDto.getMessage())
                            .build())
                    .putData("userId", String.valueOf(chatDto.getUserId()))
                    .putData("messageId", chatDto.getMessageId())
                    .putData("fitGroupId", String.valueOf(chatDto.getFitgroupId()))
                    .putData("fitMateId", String.valueOf(userId))
                    .putData("message", chatDto.getMessage())
                    .putData("messageTime", chatDto.getMessageTime())
                    .putData("messageType", String.valueOf(chatDto.getMessageType()))
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setTitle(String.valueOf(chatDto.getUserId()))
                                    .setBody(chatDto.getMessage())
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(42)
                                    .build())
                            .build())
                    .build();
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("FCM send-" + response);
            } catch (FirebaseMessagingException e) {
                log.info("FCM except-" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
