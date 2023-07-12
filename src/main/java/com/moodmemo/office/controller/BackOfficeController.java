package com.moodmemo.office.controller;

import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.service.DailyReportService;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Tag(name = "[백오피스용 API]", description = "** 프론트엔드 봐주세요! **")
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class BackOfficeController {

    // TODO - 에러처리해야함
    // Todo - 날짜 변경할 수 있는 메소드 필요
    private final UserService userService;
    private final StampService stampService;
    private final DailyReportService dailyReportService;

    @Operation(summary = "AI 서버로부터 일기를 받아오는 메소드", description = "카카오아이디만 보내주시면 됩니다!")
    @GetMapping(value = "/dailyReport/{kakaoId}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReportFromAI(
            @PathVariable final String kakaoId) {
        // 새벽 3시 이후, "어제" 03:00 ~ 오늘 02:59 사이의 스탬프리스트를 가져온다.
        return stampService.createDailyReport(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @Operation(summary = "DB에 저장된 <어제> 일기를 가져오는 메소드", description = "날짜 변경 가능해지면 수정될 예정입니다!")
    @GetMapping(value = "/dailyReport/final/{kakaoId}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReportDBVersionYesterday(
            @PathVariable final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return dailyReportService.getDailyReportDBVersion(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @Operation(summary = "DB에 저장된 일기를 가져오는 메소드", description = "FE testing")
    @GetMapping(value = "/dailyReport/final/{kakaoId}/{date}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReportDBVersion(
            @PathVariable(value = "date") final String date,
            @PathVariable(value = "kakaoId") final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return dailyReportService.tmp(
                kakaoId, date);
    }

    @Operation(summary = "메인 화면-유저 이름, 아이디, 어제 let 의 개수")
    @GetMapping("/userStampCount")
    public HashMap<String, Object> getUserStampCountList() {
        // 새벽 3시 이후, "어제" 03:00 ~ 오늘 02:59 사이의 스탬프리스트를 가져온다.
        return userService.getUserStampCount(
                LocalDate.now().minusDays(1));
    }

    @Operation(summary = "어제 유저가 찍은 스탬프리스트를 가져오는 메소드", description = "카카오아이디만 보내주시면 됩니다!")
    @GetMapping("/userStampAndLet/{kakaoId}")
    public List<StampDto.Office> getUserStampAndLet(@PathVariable final String kakaoId) {
        // 새벽 3시 이후, "어제" 03:00 ~ 오늘 02:59 사이의 스탬프리스트를 가져온다.
        return userService.getUserStampAndLet(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @Operation(summary = "어제 유저가 찍은 사진 리스트를 가져오는 메소드", description = "카카오아이디만 보내주시면 됩니다!")
    @GetMapping("/imageLet/{kakaoId}")
    public List<StampDto.Image> getImageLet(@PathVariable final String kakaoId) {
        // 새벽 3시 이후, "어제" 03:00 ~ 오늘 02:59 사이의 스탬프리스트를 가져온다.
        return userService.getImageLet(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @Operation(summary = "백오피스용 DR 업데이트 메소드")
    @PostMapping("/dailyReport")
    public HttpStatus upsertDailyReport(@RequestBody DailyReportDto.Response dr) {
        return dailyReportService.upsertDailyReport(dr);
    }


}
