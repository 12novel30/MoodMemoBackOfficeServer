package com.moodmemo.office.service;

import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.StampRepository;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.moodmemo.office.code.OfficeCode.ENDDATE_TAIL;
import static com.moodmemo.office.code.OfficeCode.STARTDATE_TAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class StampService {
    private final StampRepository stampRepository;
    private final UserRepository userRepository;

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

        // Todo - AI 에 어떻게 보내?;
        DailyReportDto.Request toAI = getDailyReportRequestDto(kakaoId);

        // Todo - AI 에서 받은 결과를 바탕으로 DailyReportDto.Response 를 만들어서 반환
        return DailyReportDto.Response.builder()
                .kakaoId(kakaoId)
                .date(LocalDate.now())
                .username("이하은")
                .title("오늘의 일기")
                .bodyText("진짜 .. 진짜 자고 싶다 공부 너무 하기 싫다 아악 ... 시험 왜 보냐 진짜 ...")
                .keyword(List.of("지겨움", "시험"))
                .build();
    }

    private DailyReportDto.Request getDailyReportRequestDto(String kakaoId) {
        return DailyReportDto.Request.builder()
                .userDto(UserDto.Detail.fromDocuments(
                        userRepository.findByKakaoId(kakaoId)))
                .todayStampList(
                        stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                                kakaoId,
                                Timestamp.valueOf(
                                        LocalDate.now().minusDays(1)
                                                + STARTDATE_TAIL.getDescription()),
                                Timestamp.valueOf(
                                        LocalDate.now().plusDays(1)
                                                + ENDDATE_TAIL.getDescription())))
                .build();
    }
}
