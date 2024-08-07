package com.fitmate.oauth.controller;

import com.fitmate.oauth.dto.alarmDto.ChatDto;
import com.fitmate.oauth.dto.alarmDto.PenaltyCompleteDto;
import com.fitmate.oauth.service.alarm.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlarmController {

    private final WebhookService webhookService;

    @PostMapping("/wallet/penalty-complete")
    public void penaltyComplete(@RequestBody PenaltyCompleteDto penaltyCompleteDto) {
        // wallet 서버 웹훅
        webhookService.sendPenaltyCompleteWebhook(penaltyCompleteDto);
    }

    @PostMapping("/chat/real-time-chat")
    public void realTimeChat(@RequestBody ChatDto chatDto) {
        // chat 서버 웹훅
        webhookService.sendRealTimeChatWebhook(chatDto);
    }

}
