package com.moodmemo.office.service;

import com.moodmemo.office.domain.DailyReport;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;

    public ResponseEntity upsertDailyReport(DailyReportDto.Response dr) {
        DailyReport dailyReport = null;

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
            if (dr.getKeyword() != null)
                dailyReport.setKeyword(dr.getKeyword());
        } else {
            dailyReport = DailyReport.builder()
                    .kakaoId(dr.getKakaoId())
                    .date(dr.getDate())
                    .title(dr.getTitle())
                    .keyword(dr.getKeyword())
                    .bodyText(dr.getBodyText())
                    .build();
        }
        return ResponseEntity.ok(dailyReportRepository.save(dailyReport));
    }
}
