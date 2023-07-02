package com.moodmemo.office.controller;

import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {
    private final DailyReportService dailyReportService;

    @GetMapping(value = "/dailyReport/user/{kakaoId}/{date}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReportToUser(
            @PathVariable(value = "date") final String date,
            @PathVariable(value = "kakaoId") final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return dailyReportService.getDailyReportDBVersionToUser(
                kakaoId, LocalDate.parse(date));
    }

    @PutMapping(value = "/dailyReport/user",
            produces = "application/json;charset=UTF-8")
    public HttpStatus updateDailyReportByUser(@RequestBody DailyReportDto.Response dr) {
        return dailyReportService.updateDailyReport(dr);
    }

    @PutMapping(value = "/dailyReport/like",
            produces = "application/json;charset=UTF-8")
    public HttpStatus updateLikeCnt(@RequestBody DailyReportDto.Simple simple) {
        return dailyReportService.updateLikeCnt(simple);
    }
}
