package com.moodmemo.office.service;

import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.StampRepository;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.moodmemo.office.code.EventCode.*;
import static com.moodmemo.office.code.OfficeCode.ENDDATE_TAIL;
import static com.moodmemo.office.code.OfficeCode.STARTDATE_TAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class StampService {
    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    public StampDto.Response createStamp(StampDto.Dummy request) {
        LocalDateTime ldt = request.getDateTime();
        return StampDto.Response.fromDocument(
                stampRepository.save(
                        Stamps.builder()
                                .kakaoId(request.getKakaoId())
                                .localDate(LocalDate.of(
                                        ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth()))
                                .localTime(LocalTime.of(
                                        ldt.getHour(), ldt.getMinute()))
                                .dateTime(request.getDateTime())
                                .stamp(request.getStamp())
                                .memoLet(request.getMemoLet())
                                .build())
        );

    }

    public DailyReportDto.Response createDailyReport(String kakaoId) {
        return aiService.sendDailyReport(getDailyReportRequestDto(kakaoId));
    }

    private DailyReportDto.Request getDailyReportRequestDto(String kakaoId) {

        // 자정이 넘은 뒤, 어제의 스탬프리스트들을 가져오는 것으로 생각함.
        LocalDate yesterday = LocalDate.now().minusDays(1);

        return DailyReportDto.Request.builder()
                .userDto(UserDto.SendAI.fromDocuments(
                        userRepository.findByKakaoId(kakaoId)))
                .todayStampList(getStampListByDate(kakaoId, yesterday))
                .build();
    }

    public List<Stamps> getStampListByDate(String kakaoId) {
        // 오늘의 스탬프리스트를 요청.
        LocalDate today = LocalDate.now();
        return getStampListByDate(kakaoId, today);
    }

    private List<Stamps> getStampListByDate(String kakaoId, LocalDate date) {
        return stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                kakaoId,
                Timestamp.valueOf(
                        date.minusDays(1)
                                + STARTDATE_TAIL.getDescription()),
                Timestamp.valueOf(
                        date.plusDays(1)
                                + ENDDATE_TAIL.getDescription()));
    }

    public int validateWeek() {
        List<List<LocalDate>> weeks = List.of(
                List.of(WEEK1.getStartDate(), WEEK1.getEndDate()),
                List.of(WEEK2.getStartDate(), WEEK2.getEndDate()),
                List.of(WEEK3.getStartDate(), WEEK3.getEndDate()),
                List.of(WEEK4.getStartDate(), WEEK4.getEndDate()));

        LocalDate today = LocalDate.now();
        log.info(today.toString());
        log.info(WEEK1.getStartDate().toString());
        for (List<LocalDate> week : weeks) {
            if (validateIsInWeekRange(week, today)) {
                int weekNum = weeks.indexOf(week) + 1;
                log.info("현재 주차: {}", weekNum);
                return weekNum;
            }
        }
        return 0;
    }

    private static boolean validateIsInWeekRange(List<LocalDate> week, LocalDate today) {
        return (today.isAfter(week.get(0)) && today.isBefore(week.get(1)))
                || today.isEqual(week.get(0))
                || today.isEqual(week.get(1));
    }

    public List<UserDto.Rank> getTop1ForThisWeek(int validateWeek) {
        if (validateWeek == 1)
            return convertorUsersToRank(userRepository.findTop1ByOrderByWeek1Desc());
        else if (validateWeek == 2)
            return convertorUsersToRank(userRepository.findTop1ByOrderByWeek2Desc());
        else if (validateWeek == 3)
            return convertorUsersToRank(userRepository.findTop1ByOrderByWeek3Desc());
        else if (validateWeek == 4)
            return convertorUsersToRank(userRepository.findTop1ByOrderByWeek4Desc());
        else {
            log.info("현재 주차가 없습니다.");
            return null;
        }
    }

    private List<UserDto.Rank> convertorUsersToRank(List<Users> usersList) {
        return usersList.stream()
                .map(UserDto.Rank::fromDocument)
                .collect(Collectors.toList());
    }
}
