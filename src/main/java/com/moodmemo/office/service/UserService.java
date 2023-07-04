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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.moodmemo.office.code.KakaoCode.EVENT_WEEK1_GIFT_SEND_DAY;
import static com.moodmemo.office.code.OfficeErrorCode.NO_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final StampService stampService;

    private final DateTimeFormatter rankToBotFormat =
            DateTimeFormatter.ofPattern("MM/dd HH:mm");
    private final DateTimeFormatter drDateFormat =
            DateTimeFormatter.ofPattern("YYYY-MM-dd");

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

    public void updateWeekCount(String kakaoId, int weekNum, int cnt) {
        Users user = getUser(kakaoId);

        if (weekNum == 1)
            user.setWeek1(user.getWeek1() + cnt);
        else if (weekNum == 2)
            user.setWeek2(user.getWeek2() + cnt);
        else if (weekNum == 3)
            user.setWeek3(user.getWeek3() + cnt);
        else if (weekNum == 4)
            user.setWeek4(user.getWeek4() + cnt);

        userRepository.save(user);
    }

    private Users getUser(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new OfficeException(NO_USER));
    }

    private String getAboutRankingFormat(int weekNum,
                                         String str_standard,
                                         String str_errorForWeekNum,
                                         String str_endingForWinner,
                                         String str_endingForLoser,
                                         String kakaoId) {

        int myStampCount;
        String returnFormat = weekNum + "주차 랭킹"
                + str_standard
                + "\n==========\n";

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
                else return str_errorForWeekNum;

                returnFormat += "축하드립니다! 총 스탬프 " + myStampCount + "개로 1등입니다."
                        + "\n\n" + str_endingForWinner;
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
        } else return str_errorForWeekNum;

        returnFormat +=
                "현재 1등 : " + top1StampCount + "개"
                        + "\n내 스탬프 개수 : " + myStampCount + "개"
                        + "\n\n" + str_endingForLoser;
        return returnFormat;
    }

    public String tmpRnag(String kakaoId) {
        int weekNum = stampService.validateWeek();
        String str_standard = " ("
                + LocalDateTime.now().format(rankToBotFormat)
                + " 기준)";
        String str_errorForWeekNum = "지금은 이벤트 기간이 아니다무!";
        String str_endingForWinner = "앞으로도 스탬프 많이 남겨서 1등을 지키길 바란다무!✨";
        String str_endingForLoser = "스탬프 더 많이 남겨서 1등 탈환하길 바란다무!🔥";

        return tmp(
                weekNum,
                str_standard,
                str_errorForWeekNum,
                str_endingForWinner,
                str_endingForLoser,
                kakaoId);
    }

    private String tmp(int weekNum,
                       String str_standard,
                       String str_errorForWeekNum,
                       String str_endingForWinner,
                       String str_endingForLoser,
                       String kakaoId) {

        int myStampCount;
        String returnFormat = "🥬 " + weekNum + "주차 랭킹"
                + str_standard + " 🥬"
                + "\n==========\n";

        List<UserDto.Rank> top1ForThisWeek =
                stampService.getTop1ForThisWeek(weekNum);

        for (UserDto.Rank top : top1ForThisWeek) {
            // TODO - 이거 진짜 1개만 나오는지 확인할 것
            // 1등인 경우
            int secondCount;
            if (top.getKakaoId().equals(kakaoId)) {
                if (weekNum == 1) {
                    myStampCount = top.getWeek1();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek1Desc()
                            .stream()
                            .map(Users::getWeek1)
                            .collect(Collectors.toList()));
                } else if (weekNum == 2) {
                    myStampCount = top.getWeek2();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek2Desc()
                            .stream()
                            .map(Users::getWeek2)
                            .collect(Collectors.toList()));
                } else if (weekNum == 3) {
                    myStampCount = top.getWeek3();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek3Desc()
                            .stream()
                            .map(Users::getWeek3)
                            .collect(Collectors.toList()));
                } else if (weekNum == 4) {
                    myStampCount = top.getWeek4();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek4Desc()
                            .stream()
                            .map(Users::getWeek4)
                            .collect(Collectors.toList()));
                } else return str_errorForWeekNum;

                returnFormat += "축하한다무! 총 스탬프 " + myStampCount + "개로 1등이다무." +
                        "\n현재 2등의 개수는 🤫" + secondCount + "개!🤫"
                        + "\n\n" + str_endingForWinner;
                return returnFormat;
            }
        }

        // 1등 아닌 경우
        int inFrontOfMe = 0;
        if (weekNum == 1) {
            myStampCount = getUser(kakaoId).getWeek1();
            List<Users> usersList = userRepository.findAllByOrderByWeek1Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        } else if (weekNum == 2) {
            myStampCount = getUser(kakaoId).getWeek2();
            List<Users> usersList = userRepository.findAllByOrderByWeek2Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        } else if (weekNum == 3) {
            myStampCount = getUser(kakaoId).getWeek3();
            List<Users> usersList = userRepository.findAllByOrderByWeek3Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        } else if (weekNum == 4) {
            myStampCount = getUser(kakaoId).getWeek4();
            List<Users> usersList = userRepository.findAllByOrderByWeek4Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        } else return str_errorForWeekNum;


        returnFormat += "현재 " + getUser(kakaoId).getUsername() + "님 앞에 👀"
                + inFrontOfMe + " 명이 있다무...!👀"
                + "\n(내 스탬프 개수 : " + myStampCount + "개)"
                + "\n\n" + str_endingForLoser;
        return returnFormat;
    }

    private int getSecondPlayerCount(List<Integer> orderedUserList) {
        LinkedHashSet<Integer> linkedSet = new LinkedHashSet<>(orderedUserList);
        Iterator<Integer> iterator = linkedSet.iterator();
        iterator.next(); // 첫 번째 요소 건너뛰기
        return iterator.next();
    }

    public String getMyRanking(String kakaoId) {
        int weekNum = stampService.validateWeek();
        String str_standard = " ("
                + LocalDateTime.now().format(rankToBotFormat)
                + " 기준)";
        String str_errorForWeekNum = "현재는 이벤트 기간이 아닙니다!";
        String str_endingForWinner = "앞으로도 많은 스탬프를 남겨 1등을 지키시길 바라요!🥰";
        String str_endingForLoser = "더 많은 스탬프를 남겨 1등을 탈환하길 바라요!🥰";

        return getAboutRankingFormat(
                weekNum,
                str_standard,
                str_errorForWeekNum,
                str_endingForWinner,
                str_endingForLoser,
                kakaoId);
    }

    public String getPrizePostWeek(String kakaoId) {
        int weekNum = stampService.validateWeek() - 1; // 지난주의 랭킹 확인
        String str_standard = " (fin)";
        String str_errorForWeekNum = "이벤트 기간이 아닙니다!";
        String str_endingForWinner = "MoodMemo 서비스를 열심히 이용해주셔서 정말 감사드립니다!" +
                "\n감사의 의미를 담아, 1등 경품인 🍔맘스터치🍔 기프티콘은 " +
                EVENT_WEEK1_GIFT_SEND_DAY.getDescription() +
                "에 발송드리도록 하겠습니다." +
                "\n더 업그레이드 된 이번 주차에서도 많은 스탬프를 남겨 다시 한 번 1등에 도전하시길 바라요!🥰";
        String str_endingForLoser = "아쉽게도 지난 주차에서는 1위를 하지 못했어요😓" +
                "\n\n하지만 걱정하지 마세요!" +
                "\n지난 주의 기록과는 별개로, 새로운 주간에 찍은 스탬프로 다시 한 번 1등에 도전할 수 있답니다🔥" +
                "\n\n더 업그레이드 된 MoodMemo 와 함께 하루를 기록해보세요!😀";

        return getAboutRankingFormat(
                weekNum,
                str_standard,
                str_errorForWeekNum,
                str_endingForWinner,
                str_endingForLoser,
                kakaoId);
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
        // 어제의 스탬프가 2개 이상일 때에만 일기 생성
        if (getStampCount(kakaoId, date) >= 2) {
            String strDate = date.format(drDateFormat);
            return "🥬 Moo가 데일리 레포트 완성했다무! 🥬" +
                    "\n\n" + strDate + "의 일기는" +
                    "\n아래 링크를 클릭하면 확인 & 수정할 수 있다무 ✨" +
                    "\n\n링크: " +
                    "http://3.34.55.218/dailyReport/" +
                    getUser(kakaoId).getId() + "/" +
                    strDate;
        } else return "🥬 : 어제 일기는 못 만들었다무.. 💦" +
                "\n오늘은 스탬프 2개 이상 남겨줘라무 !";
    }

    @Transactional(readOnly = true)
    private int getStampCount(String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange =
                stampService.getTimeRangeByOneDay(date);
        return stampRepository.countByKakaoIdAndDateTimeBetween(
                kakaoId, timeRange.get(0), timeRange.get(1));
    }

    @Transactional(readOnly = true)
    public List<StampDto.Office> getUserStampAndLet(
            String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = stampService.getTimeRangeByOneDay(date);
        return stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                        kakaoId, timeRange.get(0), timeRange.get(1))
                .stream()
                .map(StampDto.Office::fromDocument)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Users> getuserEntityAll() {
        return userRepository.findAll();
    }
}
