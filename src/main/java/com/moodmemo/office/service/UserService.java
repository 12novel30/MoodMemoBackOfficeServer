package com.moodmemo.office.service;

import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.StampRepository;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.moodmemo.office.code.OfficeCode.ENDDATE_TAIL;
import static com.moodmemo.office.code.OfficeCode.STARTDATE_TAIL;

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

    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto.Response::fromDocuments)
                .collect(Collectors.toList());
    }

    public void updateWeekCount(String kakaoId, int weekNum) {
        // TODO - 에러처리하기
        if (userRepository.findByKakaoId(kakaoId) != null) {
            Users user = userRepository.findByKakaoId(kakaoId);
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
    }
    private final DateTimeFormatter rankToBotFormat =
            DateTimeFormatter.ofPattern("HH:mm");

    public String getMyRanking(String kakaoId) {

        int myStampCount = 0;
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
        int top1StampCount = 0;
        if (weekNum == 1) {
            top1StampCount = top1ForThisWeek.get(0).getWeek1();
            myStampCount = userRepository.findWeek1ByKakaoId(kakaoId).getWeek1();
        } else if (weekNum == 2) {
            top1StampCount = top1ForThisWeek.get(0).getWeek2();
            myStampCount = userRepository.findWeek2ByKakaoId(kakaoId).getWeek2();
        } else if (weekNum == 3) {
            top1StampCount = top1ForThisWeek.get(0).getWeek3();
            myStampCount = userRepository.findWeek3ByKakaoId(kakaoId).getWeek3();
        } else if (weekNum == 4) {
            top1StampCount = top1ForThisWeek.get(0).getWeek4();
            myStampCount = userRepository.findWeek4ByKakaoId(kakaoId).getWeek4();
        } else return "현재는 이벤트 기간이 아닙니다!";

        returnFormat +=
                "현재 1등 : " + top1StampCount + "개" +
                        "\n내 스탬프 개수 : " + myStampCount + "개" +
                        "\n\n더 많은 스탬프를 남겨 1등을 탈환하길 바라요!🥰";
        return returnFormat;
    }

    public boolean validateUserAlreadyExist(String kakaoId) {
        if (userRepository.findByKakaoId(kakaoId) != null)
            return true; // 이미 있다
        return false; // 없다
    }

    public HashMap<String, Object> getUserStampCountYesterday() {
        List<UserDto.StampCount> stampCountList = userRepository.findAll()
                .stream()
                .map(UserDto.StampCount::fromDocuments)
                .collect(Collectors.toList());

        // 자정이 넘은 뒤, 어제의 스탬프리스트들을 가져오는 것으로 생각함.
        LocalDate yesterday = LocalDate.now().minusDays(1);
        for (UserDto.StampCount stampCount : stampCountList) {
            stampCount.setStampCount(
                    stampRepository.countByKakaoIdAndDateTimeBetween(
                            stampCount.getKakaoId(),
                            Timestamp.valueOf(
                                    yesterday.minusDays(1)
                                            + STARTDATE_TAIL.getDescription()),
                            Timestamp.valueOf(
                                    yesterday.plusDays(1)
                                            + ENDDATE_TAIL.getDescription())));
        }

        HashMap<String, Object> resultJson = new HashMap<>();
        resultJson.put("info", yesterday + "의 스탬프 개수");
        resultJson.put("data", stampCountList);

        return resultJson;
    }
}
