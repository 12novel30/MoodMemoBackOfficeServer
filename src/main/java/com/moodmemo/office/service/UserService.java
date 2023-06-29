package com.moodmemo.office.service;

import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.exception.OfficeException;
import com.moodmemo.office.repository.StampRepository;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.moodmemo.office.code.OfficeCode.ENDDATE_TAIL;
import static com.moodmemo.office.code.OfficeCode.STARTDATE_TAIL;
import static com.moodmemo.office.code.OfficeErrorCode.NO_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final StampRepository stampRepository;

    private final UserRepository userRepository;
    private final StampService stampService;

    public UserDto.Response createUser(UserDto.Dummy request) {

        return UserDto.Response.fromDocuments(
                userRepository.save(
                        Users.builder()
                                .age(request.getAge())
                                .kakaoId(request.getKakaoId())
                                .username(request.getUsername())
                                .job(request.getJob())
                                .gender(request.isGender())
                                .build())
        );

    }

    @Transactional(readOnly = true)
    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto.Response::fromDocuments)
                .collect(Collectors.toList());
    }

    public void updateWeekCount(String kakaoId, int weekNum) {
        Users user = getUser(kakaoId);
        if (weekNum == 1)
            user.setWeek1(user.getWeek1() + 1);
        else if (weekNum == 2)
            user.setWeek2(user.getWeek2() + 1);
        else if (weekNum == 3)
            user.setWeek3(user.getWeek3() + 1);
        else if (weekNum == 4)
            user.setWeek4(user.getWeek4() + 1);

        userRepository.save(user);
    }

    private Users getUser(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new OfficeException(NO_USER));
    }

    private final DateTimeFormatter rankToBotFormat =
            DateTimeFormatter.ofPattern("MM/dd HH:mm");

    public String getMyRanking(String kakaoId) {

        int myStampCount;
        int weekNum = stampService.validateWeek();
        String returnFormat = weekNum + "주차 랭킹 (" +
                LocalDateTime.now().format(rankToBotFormat) +
                " 기준)" + "\n==========\n";

        List<UserDto.Rank> top1ForThisWeek =
                stampService.getTop1ForThisWeek(weekNum);

        for (UserDto.Rank top : top1ForThisWeek)
            // 1등인 경우
            if (top.getKakaoId().equals(kakaoId)) {
                // TODO - 2등 몇개인지 찾기 기능은 ... 일단 미뤄두기
                if (weekNum == 1)
                    myStampCount = top.getWeek1();
                else if (weekNum == 2)
                    myStampCount = top.getWeek2();
                else if (weekNum == 3)
                    myStampCount = top.getWeek3();
                else if (weekNum == 4)
                    myStampCount = top.getWeek4();
                else return "현재는 이벤트 기간이 아닙니다!";

                returnFormat += "축하드립니다! 총 스탬프 " + myStampCount + "개로 1등입니다." +
                        "\n\n앞으로도 많은 스탬프를 남겨 1등을 지키시길 바라요!🥰";
                return returnFormat;
            }

        // 1등 아닌 경우
        int top1StampCount;
        if (weekNum == 1) {
            top1StampCount = top1ForThisWeek.get(0).getWeek1();
            myStampCount = getUser(kakaoId).getWeek1();
        } else if (weekNum == 2) {
            top1StampCount = top1ForThisWeek.get(0).getWeek2();
            myStampCount = getUser(kakaoId).getWeek2();
        } else if (weekNum == 3) {
            top1StampCount = top1ForThisWeek.get(0).getWeek3();
            myStampCount = getUser(kakaoId).getWeek3();
        } else if (weekNum == 4) {
            top1StampCount = top1ForThisWeek.get(0).getWeek4();
            myStampCount = getUser(kakaoId).getWeek4();
        } else return "현재는 이벤트 기간이 아닙니다!";

        returnFormat +=
                "현재 1등 : " + top1StampCount + "개" +
                        "\n내 스탬프 개수 : " + myStampCount + "개" +
                        "\n\n더 많은 스탬프를 남겨 1등을 탈환하길 바라요!🥰";
        return returnFormat;
    }

    public boolean validateUserAlreadyExist(String kakaoId) {
        if (userRepository.findByKakaoId(kakaoId).isPresent())
            return true; // 이미 있다
        return false; // 없다
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getUserStampCount(LocalDate date) {

        List<UserDto.StampCount> userDtoList =
                userRepository.findAllByOrderByUsername()
                        .stream()
                        .map(UserDto.StampCount::fromDocuments)
                        .collect(Collectors.toList());

        for (UserDto.StampCount userDto : userDtoList)
            userDto.setStampCount(getStampCount(userDto.getKakaoId(), date));

        HashMap<String, Object> resultJson = new HashMap<>();
        resultJson.put("info", date + "의 스탬프 개수");
        resultJson.put("data", userDtoList);

        return resultJson;
    }

    public String getUserDRYesterday(String kakaoId, LocalDate date) {
        if (getStampCount(kakaoId, date) >= 2) {
            String strDate = date.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
            return "🔔데일리 레포트 완성🔔" +
                    "\n\nMoodMemo AI가 " + strDate + "의 일기를 완성했어요🎉" +
                    "\n아래 링크를 클릭하시면 확인 및 수정하실 수 있답니다😀" +
                    "\n\n링크: " +
                    "http://3.34.55.218/dailyReport/" +
                    getUser(kakaoId).getId() + "/" +
                    strDate;
        } else return "🔔데일리 레포트 미완성🔔" +
                "\n\n어제 남겨주신 let의 개수가 2개 미만이라" +
                "\nMoodMemo AI가 일기를 만들어드리지 못했어요..." +
                "\n오늘은 하루 2개 이상의 let을 남기고 AI 일기를 받아보세요!";
    }

    private int getStampCount(String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = stampService.getTimeRangeByOneDay(date);
        return stampRepository.countByKakaoIdAndDateTimeBetween(
                kakaoId, timeRange.get(0), timeRange.get(1));
    }

    public List<StampDto.Office> getUserStampAndLet(
            String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = stampService.getTimeRangeByOneDay(date);
        return stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                        kakaoId, timeRange.get(0), timeRange.get(1))
                .stream()
                .map(StampDto.Office::fromDocument)
                .collect(Collectors.toList());
    }
}
