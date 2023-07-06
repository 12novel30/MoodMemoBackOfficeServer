package com.moodmemo.office.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moodmemo.office.code.EventCode.WEEK1;
import static com.moodmemo.office.code.OfficeCode.LOCAL_FOLDER;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class DummyController {

    private final UserService userService;
    private final StampService stampService;
    private final KakaoService kakaoService;
    private final S3UploaderService s3UploaderService;
    private final S3Uploader2 s3Uploader2;

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

    @PostMapping("/kakao-image")
    public String kakaoImage(@RequestBody final String imageUrl) {
        return s3UploaderService.kakaoImageUrlSaveToLocal(
                imageUrl, "testImageName");
    }

//    @PostMapping("/kakao-image/2")
//    public MultipartFile kakaoImage2(@RequestBody final String imageUrl) throws URISyntaxException, IOException {
//        return s3UploaderService.downloadPhoto(imageUrl);
//    }



    @GetMapping("/kakao-image/convert")
    public MultipartFile kakaoImage2() throws IOException {
        return s3UploaderService.convertFileToMultipartFile("testImageName");
    }
    @DeleteMapping("/kakao-image/delete")
    public void kakaoImage3() throws IOException {
        s3UploaderService.deleteLocalImage("testImageName");
    }

    // create uploadFileToS3 method
    @PostMapping("/kakao-image/upload")
    public void uploadFileToS3() throws IOException {
        File file = new File(LOCAL_FOLDER.getDescription() + "/" + "testImageName" + ".jpg");
        if (file.exists())
            log.info("file exists");
        else
            log.info("file not exists");
        s3UploaderService.store();
    }

    @GetMapping(value = "/statistics/user/{kakaoId}",
            produces = "application/json;charset=UTF-8")
    public HashMap<String, Object> getDailyReportFromAI(
            @PathVariable final String kakaoId) throws JsonProcessingException {
        return kakaoService.getStringObjectHashMap(
                userService.getStatistics(kakaoId));
    }
}
