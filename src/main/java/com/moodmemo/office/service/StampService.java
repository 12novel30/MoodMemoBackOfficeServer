package com.moodmemo.office.service;

import com.moodmemo.office.domain.Stamps;
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
                .todayStampList(getStampList(kakaoId, yesterday))
                .build();
    }

    public List<Stamps> getStampList(String kakaoId) {
        // 오늘의 스탬프리스트를 요청.
        LocalDate today = LocalDate.now();
        return getStampList(kakaoId, today);
    }

    private List<Stamps> getStampList(String kakaoId, LocalDate date) {
        return stampRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                kakaoId,
                Timestamp.valueOf(
                        date.minusDays(1)
                                + STARTDATE_TAIL.getDescription()),
                Timestamp.valueOf(
                        date.plusDays(1)
                                + ENDDATE_TAIL.getDescription()));
    }
}
