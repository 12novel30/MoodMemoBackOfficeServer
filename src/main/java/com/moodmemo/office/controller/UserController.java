package com.moodmemo.office.controller;

import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {
    private final DailyReportService dailyReportService;

    @PutMapping(value = "/dailyReport", produces = "application/json;charset=UTF-8")
    public void updateDailyReportByUser(@RequestBody DailyReportDto.Response dr) {
        dailyReportService.updateDailyReportByUser(dr);
    }
}
