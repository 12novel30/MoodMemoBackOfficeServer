package com.moodmemo.office.service;

import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.DailyReportDto;
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
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.moodmemo.office.code.EventCode.*;
import static com.moodmemo.office.code.OfficeErrorCode.NO_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class StampService {
    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    public StampDto.Response createStamp(StampDto.Dummy request) {
        Stamps stamp = Stamps.builder()
                .kakaoId(request.getKakaoId())
                .dateTime(request.getDateTime())
                .stamp(request.getStamp())
                .memoLet(request.getMemoLet())
                .build();
        if (request.getImageUrl() != null) {
            stamp.setImageUrl(request.getImageUrl());
        }
        return StampDto.Response.fromDocument(stampRepository.save(stamp)
        );

    }

    public DailyReportDto.Response createDailyReport(String kakaoId,
                                                     LocalDate date) {
        DailyReportDto.Response response = aiService.sendDailyReport(
                getDailyReportRequestDto(kakaoId, date));
        log.info("!!finish creating!!");
        return response;
    }

    @Transactional(readOnly = true)
    private DailyReportDto.Request getDailyReportRequestDto(String kakaoId,
                                                            LocalDate date) {
        return DailyReportDto.Request.builder()
                .userDto(
                        UserDto.SendAI.fromDocuments(
                                userRepository.findByKakaoId(kakaoId)
                                        .orElseThrow(() -> new OfficeException(NO_USER))))
                .todayStampList(
                        getStampListByDate(kakaoId, date))
                .build();
    }

    public List<Stamps> getStampListByDate(String kakaoId) {
        // 오늘의 스탬프리스트를 요청.
        LocalDate today = LocalDate.now();
        return getStampListByDate(kakaoId, today);
    }

    public List<LocalDateTime> getTimeRangeByOneDay(LocalDate date) {
        LocalTime standard = LocalTime.of(2, 0).minusNanos(1);

        if (LocalTime.now().isBefore(standard)
                && LocalTime.now().isAfter(LocalTime.of(0, 0)))
            date = date.minusDays(1);

        return List.of(
                // 해당 일자의 새벽 3시 부터
                LocalDateTime.of(date, standard),
                // 익일 새벽 02:59까지
                LocalDateTime.of(date.plusDays(1), standard)
        );
    }

    private List<Stamps> getStampListByDate(String kakaoId, LocalDate date) {
        List<LocalDateTime> timeRange = getTimeRangeByOneDay(date);
        return stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                kakaoId, timeRange.get(0), timeRange.get(1));
    }

    public int validateWeek() {
        List<List<LocalDate>> weeks = List.of(
                List.of(WEEK1.getStartDate(), WEEK1.getEndDate()),
                List.of(WEEK2.getStartDate(), WEEK2.getEndDate()),
                List.of(WEEK3.getStartDate(), WEEK3.getEndDate()),
                List.of(WEEK4.getStartDate(), WEEK4.getEndDate()),
                List.of(WEEK5.getStartDate(), WEEK5.getEndDate()));

        for (List<LocalDate> week : weeks)
            if (validateIsInWeekRange(week, LocalDate.now())) {
                int weekNum = weeks.indexOf(week) + 1;
                log.info("현재 주차: {}", weekNum);
                return weekNum;
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
        else if (validateWeek == 5)
            return convertorUsersToRank(userRepository.findTop1ByOrderByWeek5Desc());
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
