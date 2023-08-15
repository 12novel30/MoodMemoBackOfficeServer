package com.moodmemo.office.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.moodmemo.office.code.OfficeErrorCode.NO_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final StampService stampService;
    private final AIService aiService;

    private final DateTimeFormatter rankToBotFormat = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    private final DateTimeFormatter drDateFormat = DateTimeFormatter.ofPattern("YYYY-MM-dd");

    public UserDto.Response createUser(UserDto.Dummy request) {
        return UserDto.Response.fromDocuments(
                userRepository.save(
                        Users.builder()
                                .age(request.getAge())
                                .kakaoId(request.getKakaoId())
                                .username(request.getUsername())
                                .job(request.getJob())
                                .gender(request.isGender())
                                .inviteCnt(0)
                                .build()));
    }

    @Transactional(readOnly = true)
    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto.Response::fromDocuments).collect(Collectors.toList());
    }

    public void updateWeekCount(String kakaoId, int weekNum, int cnt) {
        Users user = getUser(kakaoId);

        if (weekNum == 1) user.setWeek1(user.getWeek1() + cnt);
        else if (weekNum == 2) user.setWeek2(user.getWeek2() + cnt);
        else if (weekNum == 3) user.setWeek3(user.getWeek3() + cnt);
        else if (weekNum == 4) user.setWeek4(user.getWeek4() + cnt);
        else if (weekNum == 5) user.setWeek5(user.getWeek5() + cnt);
        else if (weekNum == 7) user.setWeek99(user.getWeek99() + cnt);

        userRepository.save(user);
    }

    private Users getUser(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).orElseThrow(() -> new OfficeException(NO_USER));
    }

    private String getAboutRankingFormat(int weekNum, String str_standard, String str_errorForWeekNum, String str_endingForWinner, String str_endingForLoser, String kakaoId) {

        if( weekNum == 6) return "🥬 : 전야제 이벤트는 8/15 ~ 8/21까지라무!" +
                "\n열심히 스탬프를 찍어서 경품을 받아보자무!" +
                "\n\n(경품 기준이 궁금하다면, '경품 기준' 이라고 말해보라무!)";

        int myStampCount;
        String returnFormat;
        if ( weekNum == 7 )
            returnFormat = "🎆 전야제 - 누가누가 스탬프 많이 찍나 🎆" + str_standard + "\n==========\n";
        else
            returnFormat = weekNum + "주차 랭킹" + str_standard + "\n==========\n";

        List<UserDto.Rank> top1ForThisWeek = stampService.getTop1ForThisWeek(weekNum);

        for (UserDto.Rank top : top1ForThisWeek)
            // 1등인 경우
            if (top.getKakaoId().equals(kakaoId)) {
                // TODO - 2등 몇개인지 찾기 기능은 ... 일단 미뤄두기
                if (weekNum == 1) myStampCount = top.getWeek1();
                else if (weekNum == 2) myStampCount = top.getWeek2();
                else if (weekNum == 3) myStampCount = top.getWeek3();
                else if (weekNum == 4) myStampCount = top.getWeek4();
                else if (weekNum == 5) myStampCount = top.getWeek5();
                else if (weekNum == 7) myStampCount = top.getWeek_final();
                else return str_errorForWeekNum;

                returnFormat += "축하한다무! 총 스탬프 " + myStampCount + "개로 1등이다무."
                        + "\n\n" + str_endingForWinner;
                return returnFormat;
            }

        // 1등 아닌 경우
        int top1StampCount;
        int inFrontOfMe = 0;
        if (weekNum == 1) {
            top1StampCount = top1ForThisWeek.get(0).getWeek1();
            myStampCount = getUser(kakaoId).getWeek1();
        }
        else if (weekNum == 2) {
            top1StampCount = top1ForThisWeek.get(0).getWeek2();
            myStampCount = getUser(kakaoId).getWeek2();
        }
        else if (weekNum == 3) {
            top1StampCount = top1ForThisWeek.get(0).getWeek3();
            myStampCount = getUser(kakaoId).getWeek3();
        }
        else if (weekNum == 4) {
            top1StampCount = top1ForThisWeek.get(0).getWeek4();
            myStampCount = getUser(kakaoId).getWeek4();
        }
        else if (weekNum == 5) {
            top1StampCount = top1ForThisWeek.get(0).getWeek5();
            myStampCount = getUser(kakaoId).getWeek5();
        }
        else if (weekNum == 7) {
            myStampCount = getUser(kakaoId).getWeek99();
            List<Users> usersList = userRepository.findAllByOrderByWeek99Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        } else return str_errorForWeekNum;

        if ( weekNum == 7 ) {
            returnFormat += getUser(kakaoId).getUsername() + "님은 " + inFrontOfMe+1 + "등이다무!"
                    + "\n(내 스탬프 개수 : " + myStampCount + "개)";
            if ( inFrontOfMe < 10 ) // 내 앞에 9명 있으면 10등
                returnFormat += "\n\n" + "축하한다무! 경품 기준이 궁금하다면, '경품 기준' 이라고 말해보라무!" +
                        "\n경품 기프티콘은 빠른 시일 내로 발송하겠다무!"
                        + "\n안드로이드 유저라면 이제 앱을 다운받아보지 않겠냐무? 궁금하면 '앱으로 가기' 라고 말해보라무!🥰";
            else
                returnFormat += "\n\n" + str_endingForLoser;
        }
        else
            returnFormat +=
    //                "현재 1등 : " + top1StampCount + "개\n" +
                    "내 스탬프 개수 : " + myStampCount + "개" + "\n\n" + str_endingForLoser;
        return returnFormat;
    }

    public String getWinnerScorePostWeek() {
        int weekNum = stampService.validateWeek() - 1;
        List<UserDto.Rank> top1ForThisWeek = stampService.getTop1ForThisWeek(weekNum);
        int top1StampCount;
        if (weekNum == 1) {
            top1StampCount = top1ForThisWeek.get(0).getWeek1();
        } else if (weekNum == 2) {
            top1StampCount = top1ForThisWeek.get(0).getWeek2();
        } else if (weekNum == 3) {
            top1StampCount = top1ForThisWeek.get(0).getWeek3();
        } else if (weekNum == 4) {
            top1StampCount = top1ForThisWeek.get(0).getWeek4();
        } else if (weekNum == 5) {
            top1StampCount = top1ForThisWeek.get(0).getWeek5();
        } else return "지금은 이벤트 기간이 아니다무!";

        return "🥬 지난 주 1등의 개수는 " + top1StampCount + "개 였다무!";
    }

    public String tmpRnag(String kakaoId) {
        int weekNum = stampService.validateWeek();
        String str_standard = " (" + LocalDateTime.now().format(rankToBotFormat) + " 기준)";
        String str_errorForWeekNum = "지금은 이벤트 기간이 아니다무!";
        String str_endingForWinner = "앞으로도 스탬프 많이 남겨서 1등을 지키길 바란다무!✨";
//        String str_endingForLoser = "스탬프 더 많이 남겨서 1등 탈환하길 바란다무!🔥";
        String str_endingForLoser = "경품 기준이 궁금하면 '경품 기준' 이라고 말해보라무!🔥";

        return tmp(weekNum, str_standard, str_errorForWeekNum, str_endingForWinner, str_endingForLoser, kakaoId);
    }

    private String tmp(int weekNum, String str_standard, String str_errorForWeekNum, String str_endingForWinner, String str_endingForLoser, String kakaoId) {

        int myStampCount;
        String returnFormat;
        if ( weekNum == 7 )
            returnFormat = "🎆 전야제 - 누가누가 스탬프 많이 찍나 🎆" + str_standard + "\n==========\n";
        else
            returnFormat = weekNum + "주차 랭킹" + str_standard + "\n==========\n";
//        String returnFormat = "🥬 " + weekNum + "주차 랭킹" + str_standard + " 🥬" + "\n==========\n";

        List<UserDto.Rank> top1ForThisWeek = stampService.getTop1ForThisWeek(weekNum);

        if (top1ForThisWeek == null) return str_errorForWeekNum;

        for (UserDto.Rank top : top1ForThisWeek) {
            // TODO - 이거 진짜 1개만 나오는지 확인할 것
            // 1등인 경우
            int secondCount;
            if (top.getKakaoId().equals(kakaoId)) {
                if (weekNum == 1) {
                    myStampCount = top.getWeek1();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek1Desc().stream().map(Users::getWeek1).collect(Collectors.toList()));
                } else if (weekNum == 2) {
                    myStampCount = top.getWeek2();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek2Desc().stream().map(Users::getWeek2).collect(Collectors.toList()));
                } else if (weekNum == 3) {
                    myStampCount = top.getWeek3();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek3Desc().stream().map(Users::getWeek3).collect(Collectors.toList()));
                } else if (weekNum == 4) {
                    myStampCount = top.getWeek4();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek4Desc().stream().map(Users::getWeek4).collect(Collectors.toList()));
                } else if (weekNum == 5) {
                    myStampCount = top.getWeek5();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek5Desc().stream().map(Users::getWeek5).collect(Collectors.toList()));
                } else if (weekNum == 7) {
                    myStampCount = top.getWeek_final();
                    secondCount = getSecondPlayerCount(userRepository.findAllByOrderByWeek99Desc().stream().map(Users::getWeek99).collect(Collectors.toList()));
                } else return str_errorForWeekNum;

                returnFormat += "축하한다무! 총 스탬프 " + myStampCount + "개로 1등이다무."
                        + "\n현재 2등의 개수는 🤫" + secondCount + "개!🤫"
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
        }
        else if (weekNum == 2) {
            myStampCount = getUser(kakaoId).getWeek2();
            List<Users> usersList = userRepository.findAllByOrderByWeek2Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        }
        else if (weekNum == 3) {
            myStampCount = getUser(kakaoId).getWeek3();
            List<Users> usersList = userRepository.findAllByOrderByWeek3Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        }
        else if (weekNum == 4) {
            myStampCount = getUser(kakaoId).getWeek4();
            List<Users> usersList = userRepository.findAllByOrderByWeek4Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        }
        else if (weekNum == 5) {
            myStampCount = getUser(kakaoId).getWeek5();
            List<Users> usersList = userRepository.findAllByOrderByWeek5Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        }
        else if (weekNum == 7) {
            myStampCount = getUser(kakaoId).getWeek99();
            List<Users> usersList = userRepository.findAllByOrderByWeek99Desc();
            for (Users users : usersList)
                if (users.getKakaoId().equals(kakaoId)) break;
                else inFrontOfMe += 1;
        } else return str_errorForWeekNum;

        returnFormat += "현재 " + getUser(kakaoId).getUsername() + "님 앞에 👀" + inFrontOfMe + " 명이 있다무...!👀"
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

    public String getPrizePostWeek(String kakaoId) {
        int weekNum = stampService.validateWeek() - 1; // 지난주의 랭킹 확인
        String str_standard = " (fin)";
        String str_errorForWeekNum = "🥬 : 이벤트 기간이 아니다무!";
        String str_endingForWinner = "전야제 이벤트에 열심히 참여해줘서 고맙다무!"
                + "\n1등 경품인 " + "BBQ 황-올 🍗" + "기프티콘은 빠른 시일 내로 발송하겠다무!"
                + "\n안드로이드 유저라면 이제 앱을 다운받아보지 않겠냐무? 궁금하면 '앱으로 가기' 라고 말해보라무!🥰"; // TODO - 이제 앱으로 넘어가라 어쩌구 멘트
        String str_endingForLoser = "아쉽게도 전야제에서 순위권에 들지 못했다무.. 💦"
                + "\n\n그래도 열심히 참여해줘서 고맙다무!"
                + "\n안드로이드 유저라면 이제 앱을 다운받아보지 않겠냐무? 궁금하면 '앱으로 가기' 라고 말해보라무!🥰"
                + "\n\n(1등의 개수가 궁금하다면, [1등의 개수 확인] 이라고 말해보라무...!)";

        return getAboutRankingFormat(weekNum, str_standard, str_errorForWeekNum, str_endingForWinner, str_endingForLoser, kakaoId);
    }

    public boolean validateUserAlreadyExist(String kakaoId) {
        if (userRepository.findByKakaoId(kakaoId).isPresent()) return true; // 이미 있다
        return false; // 없다
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getUserStampCount(LocalDate date) {

        List<UserDto.StampCount> userDtoList = userRepository.findAllByOrderByUsername().stream().map(UserDto.StampCount::fromDocuments).collect(Collectors.toList());

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
            return "🥬 Moo가 일기 완성했다무! 🥬" + "\n\n" + strDate + "의 일기는" + "\n아래 링크를 클릭하면 확인 & 수정할 수 있다무 ✨" + "\n\n링크: " + "http://3.34.55.218/#/dailyReport/" + getUser(kakaoId).getKakaoId()
//                            .getId() // TODO - 나중에 변경 일단 kakaoid로
//                    + "/" + strDate
                    ;
        } else return "🥬 : 어제 일기는 못 만들었다무.. 💦"
                + "\n오늘은 스탬프 2개 이상 남겨줘라무 !";
    }

    public String getUserDR(String kakaoId, LocalDate date) {
        // 어제의 스탬프가 2개 이상일 때에만 일기 생성
        // TODO - 일기가 만들어졌으면 보내는 것으로 로직 수정
        if (getStampCount(kakaoId, date) >= 1) {
            String strDate = date.format(drDateFormat);
            return "🥬 Moo가 일기 완성했다무! 🥬" + "\n\n" + strDate + "의 일기는" + "\n아래 링크를 클릭하면 확인 & 수정할 수 있다무 ✨" + "\n\n링크: " + "http://3.34.55.218/#/dailyReport/" + getUser(kakaoId).getKakaoId()
//                            .getId() // TODO - 나중에 변경 일단 kakaoid로
                    + "/" + strDate;
        } else return "🥬 : 어제 일기는 스탬프가 없어서 못 만들었다무.. 💦"
                + "\n지금 스탬프 하나 눌러보는건 어떻겠냐무?";
    }

    @Transactional(readOnly = true)
    private int getStampCount(String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = stampService.getTimeRangeByOneDay(date);
        return stampRepository.countByKakaoIdAndDateTimeBetween(kakaoId, timeRange.get(0), timeRange.get(1));
    }

    @Transactional(readOnly = true)
    public List<StampDto.Office> getUserStampAndLet(String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = stampService.getTimeRangeByOneDay(date);
        return stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(kakaoId, timeRange.get(0), timeRange.get(1)).stream().map(StampDto.Office::fromDocument).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StampDto.Image> getImageLet(String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = stampService.getTimeRangeByOneDay(date);
        return stampRepository.findByKakaoIdAndDateTimeBetweenAndImageUrlIsNotNullOrderByDateTime(kakaoId, timeRange.get(0), timeRange.get(1)).stream().map(StampDto.Image::fromDocument).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Users> getuserEntityAll() {
        return userRepository.findAll();
    }

    public String getStatistics(String kakaoId) throws JsonProcessingException {
        Map<String, Object> response = aiService.getStatisticsFromAI(kakaoId);

        ObjectMapper mapper = new ObjectMapper();
        String returnMessage = "📊 MoodMemo 통계 📊\n\n" + "Moo가 그동안 " + getUser(kakaoId).getUsername() + "님이 찍은 스탬프를 정리해왔다무!🥬\n" + "📌 총 " + response.get("total_stamp") + "개의 스탬프를 찍었다무!\n";
        Map stamp_by_emotion = mapper.convertValue(response.get("stamp_by_emotion"), Map.class);

        // Map을 List로 변환
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(stamp_by_emotion.entrySet());

        // 값을 기준으로 정렬
        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // 정렬된 결과 출력
        for (Map.Entry<String, Integer> entry : entryList) {
            returnMessage += "\n" + entry.getKey() + " : " + entry.getValue() + "개";
        }
        return returnMessage;
    }

    public String inviteFriend(String kakaoId, String inviterNickName) {

        log.info(inviterNickName);
        Optional<Users> tmp = userRepository.findByUsername(inviterNickName);
        if (tmp.isEmpty())
            return "그런 사용자는 없다무! 초대한 사람에게 다시 물어보라무";
        else {
            if (tmp.get().getKakaoId().equals(kakaoId))
                return "자기 자신을 초대할 수 없다무!";
            Users invited = getUser(kakaoId);
            Users inviter = tmp.orElseThrow(() -> new OfficeException(NO_USER));
            invited.setInviteCnt(invited.getInviteCnt() + 1);
            inviter.setInviteCnt(inviter.getInviteCnt() + 1);

            userRepository.save(invited);
            userRepository.save(inviter);

            return "초대 완료! 🥬" +
                    "\nMoo랑 일기 잘 써보자무!";
        }
    }
}
