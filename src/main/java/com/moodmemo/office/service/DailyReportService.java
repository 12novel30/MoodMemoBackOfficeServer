package com.moodmemo.office.service;

import com.moodmemo.office.domain.DailyReport;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.exception.OfficeException;
import com.moodmemo.office.repository.DailyReportRepository;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.moodmemo.office.code.OfficeErrorCode.NO_DR;
import static com.moodmemo.office.code.OfficeErrorCode.NO_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public HttpStatus upsertDailyReport(DailyReportDto.Response dr) {
        DailyReport dailyReport = DailyReport.builder().build();

        if (dailyReportRepository
                .findByKakaoIdAndDate(dr.getKakaoId(), dr.getDate())
                .isPresent()) {
            dailyReport = dailyReportRepository
                    .findByKakaoIdAndDate(dr.getKakaoId(), dr.getDate()).get();
        }
        if (dailyReport.getUsername() == null) {
            dailyReport.setUsername(
                    userRepository.findByKakaoId(dr.getKakaoId())
                            .orElseThrow(() -> new OfficeException(NO_USER))
                            .getUsername());
        }

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
        if (dr.getTime() != null)
            dailyReport.setTime(dr.getTime());

        return ResponseEntity.ok(dailyReportRepository.save(dailyReport))
                .getStatusCode();
    }

    @Transactional(readOnly = true)
    public DailyReportDto.Response getDailyReportDBVersion(String kakaoId,
                                                           LocalDate date) {
        return DailyReportDto.Response.fromDocument(
                dailyReportRepository.findByKakaoIdAndDate(kakaoId, date)
                        .orElseThrow(() -> new OfficeException(NO_DR)));
    }

    public void updateDailyReportByUser(DailyReportDto.Response dr) {
    }

    @Transactional(readOnly = true)
    public DailyReportDto.Response getDailyReportDBVersionToUser(String id, LocalDate date) {
        return getDailyReportDBVersion(
                userRepository.findById(id)
                        .orElseThrow(() -> new OfficeException(NO_USER))
                        .getKakaoId(),
                date);
    }
}
