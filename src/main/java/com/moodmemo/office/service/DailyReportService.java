package com.moodmemo.office.service;

import com.moodmemo.office.domain.DailyReport;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.exception.OfficeException;
import com.moodmemo.office.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.moodmemo.office.code.OfficeErrorCode.NO_DR;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;

    @Transactional
    public HttpStatus upsertDailyReport(DailyReportDto.Response dr) {
        DailyReport dailyReport = null;

        if (!dailyReportRepository.findByKakaoIdAndDate(dr.getKakaoId(), dr.getDate())
                .isEmpty()) {
            if (dr.getKakaoId() != null)
                dailyReport.setKakaoId(dr.getKakaoId());
            if (dr.getDate() != null)
                dailyReport.setDate(dr.getDate().atStartOfDay());
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
                    .date(dr.getDate().atStartOfDay())
                    .title(dr.getTitle())
                    .keyword1st(dr.getKeyword1st())
                    .keyword2nd(dr.getKeyword2nd())
                    .keyword3rd(dr.getKeyword3rd())
                    .bodyText(dr.getBodyText())
                    .build();
        }

        return ResponseEntity.ok(dailyReportRepository.save(dailyReport))
                .getStatusCode();
    }

    @Transactional(readOnly = true)
    public DailyReportDto.Response getDailyReportDBVersion(
            String kakaoId, LocalDate date) {
        return DailyReportDto.Response.fromDocument(
                dailyReportRepository.findByKakaoIdAndDate(kakaoId, date)
                        .orElseThrow(() -> new OfficeException(NO_DR)));
    }

    public void updateDailyReportByUser(DailyReportDto.Response dr) {
    }
}
