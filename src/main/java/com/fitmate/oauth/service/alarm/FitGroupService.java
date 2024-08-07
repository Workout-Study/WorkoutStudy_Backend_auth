package com.fitmate.oauth.service.alarm;


import com.fitmate.oauth.dto.alarmDto.FitGroupResponse;
import com.fitmate.oauth.dto.alarmDto.FitMateDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FitGroupService {

    private final RestTemplate restTemplate;

    @Autowired
    public FitGroupService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<Long> getFitMateIdsFromExternalApi(Long fitGroupId, Long sendMateId) {
        String url = "http://223.130.156.217:8080/fit-group-service/mates/" + fitGroupId;
        ResponseEntity<FitGroupResponse> response = restTemplate.getForEntity(url, FitGroupResponse.class);
        FitGroupResponse fitGroupResponse = response.getBody();

        if (fitGroupResponse != null && fitGroupResponse.getFitMateDetails() != null) {
            return fitGroupResponse.getFitMateDetails().stream()
                    .map(FitMateDetail::getFitMateId)
                    .filter(fitMateId -> !fitMateId.equals(sendMateId))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
