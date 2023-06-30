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

    @GetMapping(value = "/dailyReport/user/{id}/{date}",
            produces = "application/json;charset=UTF-8") // TODO - 로그 찍어야 함
    public DailyReportDto.Response getDailyReportToUser(
            @PathVariable(value = "date") final String date,
            @PathVariable(value = "id") final String id) {
        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return dailyReportService.getDailyReportDBVersionToUser(
                id, LocalDate.parse(date));
    }

    @PostMapping(value = "/dailyReport/user",
            produces = "application/json;charset=UTF-8") // TODO - 로그 찍어야 함
    public HttpStatus upsertDailyReportByUser(@RequestBody DailyReportDto.Response dr) {
        return dailyReportService.upsertDailyReport(dr);
    }
}
