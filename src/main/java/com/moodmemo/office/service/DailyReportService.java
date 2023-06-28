package com.moodmemo.office.service;

import com.moodmemo.office.domain.DailyReport;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;

import static com.moodmemo.office.code.OfficeCode.ENDDATE_TAIL;
import static com.moodmemo.office.code.OfficeCode.STARTDATE_TAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;

    public HttpStatus upsertDailyReport(DailyReportDto.Response dr) {
        DailyReport dailyReport;

        String kakaoId = dr.getKakaoId();
        if (dailyReportRepository.findByKakaoId(kakaoId) != null) {
            dailyReport = dailyReportRepository.findByKakaoId(kakaoId);

            if (dr.getKakaoId() != null)
                dailyReport.setKakaoId(dr.getKakaoId());
            if (dr.getDate() != null)
                dailyReport.setDate(dr.getDate());
            if (dr.getTitle() != null)
                dailyReport.setTitle(dr.getTitle());
            if (dr.getBodyText() != null)
                dailyReport.setBodyText(dr.getBodyText());
            if (dr.getKeyword1st() != null)
                dailyReport.setKeyword1st(dr.getKeyword1st());
            if (dr.getKeyword2nd() != null)
                dailyReport.setKeyword2nd(dr.getKeyword2nd());
            if (dr.getKeyword3rd() != null)
                dailyReport.setKeyword3rd(dr.getKeyword3rd());
        } else {
            dailyReport = DailyReport.builder()
                    .kakaoId(dr.getKakaoId())
                    .date(dr.getDate())
                    .title(dr.getTitle())
                    .keyword1st(dr.getKeyword1st())
                    .keyword2nd(dr.getKeyword2nd())
                    .keyword3rd(dr.getKeyword3rd())
                    .bodyText(dr.getBodyText())
                    .build();
        }
        return ResponseEntity.ok(dailyReportRepository.save(dailyReport)).getStatusCode();
    }

    public DailyReportDto.Response getYesterDayDailyReportDBVersion(String kakaoId) {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        return DailyReportDto.Response.fromDocument(
                dailyReportRepository.findByKakaoIdAndDateTimeBetweenOrderByDateTime(
                        kakaoId,
                        Timestamp.valueOf(
                                yesterday.minusDays(1)
                                        + STARTDATE_TAIL.getDescription()),
                        Timestamp.valueOf(
                                yesterday.plusDays(1)
                                        + ENDDATE_TAIL.getDescription())));

    }
}
