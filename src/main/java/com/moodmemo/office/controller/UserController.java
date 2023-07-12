package com.moodmemo.office.controller;

import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.service.DailyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "[유저 페이지용 API]", description = "** 프론트엔드 봐주세요! **")
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {
    private final DailyReportService dailyReportService;

    @Operation(summary = "DB에 저장된 <어제> 일기를 가져오는 메소드", description = "날짜 변경 가능해지면 수정될 예정입니다!")
    @GetMapping(value = "/dailyReport/user/{kakaoId}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReportToUser(
//            @PathVariable(value = "date") final String date, // TODO - 일정 바꾸도록 date 받기
            @PathVariable(value = "kakaoId") final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return dailyReportService.getDailyReportDBVersionToUser(
                kakaoId, LocalDate.now().minusDays(1));
    }

    @Operation(summary = "DB에 저장된 일기를 사용자 페이지로 가져오는 메소드", description = "FE testing")
    @GetMapping(value = "/dailyReport/user/{kakaoId}/{date}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response tmp(
            @PathVariable(value = "date") final String date,
            @PathVariable(value = "kakaoId") final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return dailyReportService.tmp(kakaoId, date);
    }

    @PutMapping(value = "/dailyReport/user",
            produces = "application/json;charset=UTF-8")
    public HttpStatus updateDailyReportByUser(@RequestBody DailyReportDto.Response dr) {
        return dailyReportService.updateDailyReport(dr);
    }

    @PutMapping(value = "/dailyReport/like",
            produces = "application/json;charset=UTF-8") // TODO - 좋아요 이제 만들 예정이랬다
    public HttpStatus updateLikeCnt(@RequestBody DailyReportDto.Simple simple) {
        return dailyReportService.updateLikeCnt(simple);
    }
}
