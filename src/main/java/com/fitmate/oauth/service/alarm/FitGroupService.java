package com.fitmate.oauth.service.alarm;


import com.fitmate.oauth.dto.alarmDto.FitGroupResponse;
import com.fitmate.oauth.dto.alarmDto.FitMateDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FitGroupService {

    private final RestTemplate restTemplate;

    @Autowired
    public FitGroupService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<Long> getFitMateIdsFromExternalApi(int fitGroupId, Long sendMateId) {
        String url = "http://223.130.156.217:8080/fit-group-service/mates/" + fitGroupId;
        log.info("url: {}", url);
        ResponseEntity<FitGroupResponse> response = restTemplate.getForEntity(url, FitGroupResponse.class);
        FitGroupResponse fitGroupResponse = response.getBody();
        log.info("fitGroupResponse: {}", fitGroupResponse);
        if (fitGroupResponse != null && fitGroupResponse.getFitMateDetails() != null) {
            return fitGroupResponse.getFitMateDetails().stream()
                    .map(FitMateDetail::getFitMateUserId)
                    .filter(fitMateId -> !fitMateId.equals(sendMateId))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
