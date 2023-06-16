package com.moodmemo.office.controller;

import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    // TODO - 에러처리해야함

    private final UserService userService;
    private final StampService stampService;

    @GetMapping("/home")
    public List<UserDto.Response> getUserList() {
        return userService.getAllUsers();
    }

    @GetMapping("/dailyReport/{kakaoId}")
    public DailyReportDto.Response getDailyReport(@PathVariable final String kakaoId) {
        // Todo - 오늘 날짜 & 카카오 Id로 stamp list 를 조회 -> AI APi에 전달.
        // Todo - AI Api 에서 받은 결과를 FE에 전달.
        return stampService.createDailyReport(kakaoId);
        // Todo - 나중에는 날짜 변경할 수 있는 메소드 만들자
    }

}
