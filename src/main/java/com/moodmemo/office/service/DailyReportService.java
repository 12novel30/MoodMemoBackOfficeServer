package com.moodmemo.office.service;

import com.moodmemo.office.code.OfficeCode;
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

import static com.moodmemo.office.code.OfficeCode.DEV;
import static com.moodmemo.office.code.OfficeCode.USER;
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
        // save
        DailyReport dailyReport = DailyReport.builder().build();
        if (dailyReportRepository
                .findByKakaoIdAndDate(dr.getKakaoId(), dr.getDate())
                .isPresent()) { // update
            dailyReport = dailyReportRepository
                    .findByKakaoIdAndDate(dr.getKakaoId(), dr.getDate()).get();
        } else // save
            dailyReport.setUpdateByDevCnt(-1); // updateDailyReportEntity 에서 +1 -> 0

        dailyReport.setUpdateByUserCnt(0);

        return updateDailyReportEntity(dr, dailyReport, DEV);
    }

    @Transactional
    public HttpStatus updateDailyReport(DailyReportDto.Response dr) {
        return updateDailyReportEntity(
                dr,
                dailyReportRepository
                        .findByKakaoIdAndDate(dr.getKakaoId(), dr.getDate())
                        .orElseThrow(() -> new OfficeException(NO_DR)),
                USER);
    }

    @Transactional
    private HttpStatus updateDailyReportEntity(DailyReportDto.Response dr,
                                               DailyReport dailyReport,
                                               OfficeCode who) {
        // username 채우기
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

        if (who.equals(DEV))
            dailyReport.setUpdateByDevCnt(dailyReport.getUpdateByDevCnt() + 1);
        else // USER
            dailyReport.setUpdateByUserCnt(dailyReport.getUpdateByUserCnt() + 1);

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
    public DailyReportDto.Response getDailyReportDBVersionToUser(String kakaoId, LocalDate date) {
        return getDailyReportDBVersion(
                userRepository.findByKakaoId(kakaoId)
                        .orElseThrow(() -> new OfficeException(NO_USER))
                        .getKakaoId(),
                date);
    }
}
