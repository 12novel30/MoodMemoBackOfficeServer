package com.moodmemo.office.service;

import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

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
        String returnFormat = "1주차 랭킹 (" +
                LocalDateTime.now().format(rankToBotFormat) +
                " 기준)" + "\n==========\n";

        int weekNum = stampService.validateWeek();
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
            myStampCount = userRepository.findWeek1ByKakaoId(kakaoId);
        } else if (weekNum == 2) {
            top1StampCount = top1ForThisWeek.get(0).getWeek2();
            myStampCount = userRepository.findWeek2ByKakaoId(kakaoId);
        } else if (weekNum == 3) {
            top1StampCount = top1ForThisWeek.get(0).getWeek3();
            myStampCount = userRepository.findWeek3ByKakaoId(kakaoId);
        } else if (weekNum == 4) {
            top1StampCount = top1ForThisWeek.get(0).getWeek4();
            myStampCount = userRepository.findWeek4ByKakaoId(kakaoId);
        } else return "현재는 이벤트 기간이 아닙니다!";

        returnFormat +=
                "현재 1등 : " + top1StampCount + "개" +
                        "\n내 스탬프 개수 : " + myStampCount + "개" +
                        "\n\n더 많은 스탬프를 남겨 1등을 탈환하길 바라요!🥰";
        return returnFormat;
    }
}
