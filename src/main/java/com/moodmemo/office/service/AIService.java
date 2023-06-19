package com.moodmemo.office.service;

import com.moodmemo.office.dto.DailyReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {

    public DailyReportDto.Response sendDailyReport(DailyReportDto.Request toAI) {
        // http://localhost:9090/api/server/user/{userId}/name/{username}

        // http://3.39.118.25:5000/journal
        URI uri = UriComponentsBuilder
                .fromUriString("http://3.39.118.25:5000")
                .path("/journal")
                .encode()
                .build()
                // pathVariable 사용을 위한 메소드 순서대로 들어간다.
//                .expand("100","ugo")
                .toUri();
        log.info(uri.toString());

        // 아래 순서로 변환
        // http body - object - object mapper -> json - > http body json

        // toAI = request
//        UserRequest req = new UserRequest();
//        req.setName("ugo");
//        req.setAge(20);
        RestTemplate restTemplate = new RestTemplate();

        // post 의 경우 PostForEntity 를 사용한다.
        // 파라미터 = 요청 주소, 요청 바디, 응답 바디
        ResponseEntity<DailyReportDto.Response> response =
                restTemplate.postForEntity(uri, toAI, DailyReportDto.Response.class);

        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody());

        return response.getBody();
    }

}
