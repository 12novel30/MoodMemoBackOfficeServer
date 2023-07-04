package com.moodmemo.office.controller;

import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.service.KakaoService;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.moodmemo.office.code.EventCode.WEEK1;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class DummyController {

    private final UserService userService;
    private final StampService stampService;
    private final KakaoService kakaoService;

    public UserDto.Response createUser(@Valid @RequestBody UserDto.Dummy request) {
        return userService.createUser(request);
    }

    public StampDto.Response createStamp(@Valid @RequestBody StampDto.Dummy request) {
        return stampService.createStamp(request);
    }

    @GetMapping("/tmp")
    public void tmp() {
        LocalDate tmp1 = LocalDate.of(2023, 6, 23);
        LocalDate tmp2 = LocalDate.of(2023, 6, 29);
        LocalDate tmp3 = LocalDate.of(2023, 6, 25);
        if (tmp1.isAfter(WEEK1.getStartDate()) && tmp1.isBefore(WEEK1.getEndDate()))
            log.info("tmp1: {}", tmp1);
        if (tmp2.isAfter(WEEK1.getStartDate()) && tmp2.isBefore(WEEK1.getEndDate()))
            log.info("tmp2: {}", tmp2);
        if (tmp3.isAfter(WEEK1.getStartDate()) && tmp3.isBefore(WEEK1.getEndDate()))
            log.info("tmp3: {}", tmp3);

    }


    @GetMapping("/tmp2")
    public void tm2p() {
        LocalTime tmp4 = LocalTime.of(2,59);
        LocalTime tmp1 = LocalTime.of(3,0);
        LocalTime tmp2 = LocalTime.of(3,1);
        LocalTime tmp3 = LocalTime.of(23,59);
        LocalTime tmp5 = LocalTime.of(0,0);
        LocalTime tmp6 = LocalTime.of(0,1);


        if (kakaoService.validateTimeIs3AMtoMidnight(tmp4))
            log.info("{}", tmp4);
        if (kakaoService.validateTimeIs3AMtoMidnight(tmp1))
            log.info("{}", tmp1);
        if (kakaoService.validateTimeIs3AMtoMidnight(tmp2))
            log.info("{}", tmp2);
        if (kakaoService.validateTimeIs3AMtoMidnight(tmp3))
            log.info("{}", tmp3);
        if (kakaoService.validateTimeIs3AMtoMidnight(tmp5))
            log.info("{}", tmp5);
        if (kakaoService.validateTimeIs3AMtoMidnight(tmp6))
            log.info("{}", tmp6);
    }

    @GetMapping("/all")
    public List<Users> userEntityAll() {
        return userService.getuserEntityAll();
    }

    @GetMapping("/ranking/new/{kakaoId}")
    public String rankTest(@PathVariable final String kakaoId) {
        return userService.tmpRnag(kakaoId);
    }
}
