package com.moodmemo.office.controller;

import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.service.DailyReportService;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class BackOfficeController {

    // TODO - 에러처리해야함

    private final UserService userService;
    private final StampService stampService;
    private final DailyReportService dailyReportService;

//    @GetMapping("/home")
//    public List<UserDto.Response> getUserList() {
//        return userService.getAllUsers();
//    }

    @GetMapping(value = "/dailyReport/{kakaoId}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReport(
            @PathVariable final String kakaoId) {
        // Todo - 날짜 변경할 수 있는 메소드 필요
        // 자정이 넘은 뒤, 어제의 스탬프리스트들을 가져오는 것으로 생각함.
        return stampService.createDailyReport(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @GetMapping(value = "/dailyReport/final/{kakaoId}",
            produces = "application/json;charset=UTF-8")
    public DailyReportDto.Response getDailyReportDBVersion(
            @PathVariable final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 스탬프리스트들을 가져오는 것으로 생각함.
        return dailyReportService.getDailyReportDBVersion(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @GetMapping(value = "/dailyReport/{id}/{date}",
            produces = "application/json;charset=UTF-8") // TODO - 로그 찍어야 함
    public DailyReportDto.Response getDailyReportToUser(
            @PathVariable(value = "date") final String date,
            @PathVariable(value = "id") final String id) {
        return dailyReportService.getDailyReportDBVersion(
                id,
                LocalDate.now().minusDays(1));
    }

    @GetMapping("/userStampCount")
    public HashMap<String, Object> getUserStampCount() {
        // 자정이 넘은 뒤, 어제의 스탬프리스트들을 가져오는 것으로 생각함.
        return userService.getUserStampCount(
                LocalDate.now().minusDays(1));
    }

    @GetMapping("/userStampAndLet/{kakaoId}")
    public List<StampDto.Office> getUserStampAndLet(@PathVariable final String kakaoId) {
        // 자정이 넘은 뒤, 어제의 스탬프리스트들을 가져오는 것으로 생각함.
        return userService.getUserStampAndLet(
                kakaoId,
                LocalDate.now().minusDays(1));
    }

    @PostMapping(value = "/dailyReport",
            produces = "application/json;charset=UTF-8")
    // 사용자가 업데이트 할 때에도 같은 메소드 사용
    public HttpStatus upsertDailyReport(@RequestBody DailyReportDto.Response dr) {
        return dailyReportService.upsertDailyReport(dr);
    }

    @PostMapping(value = "/dailyReport/{id}/{date}",
            produces = "application/json;charset=UTF-8") // TODO - 로그 찍어야 함
    public HttpStatus upsertDailyReportByUser(@RequestBody DailyReportDto.Response dr) {
        return dailyReportService.upsertDailyReport(dr);
    }

}
