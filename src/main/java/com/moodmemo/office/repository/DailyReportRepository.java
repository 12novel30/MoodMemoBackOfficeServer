package com.moodmemo.office.repository;

import com.moodmemo.office.domain.DailyReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyReportRepository extends MongoRepository<DailyReport, String> {

    Optional<DailyReport> findByKakaoIdAndDateTimeBetweenOrderByDateTime(
            String kakaoId,
            Timestamp startDateTime,
            Timestamp endDateTime);

    Optional<DailyReport> findByKakaoId(String kakaoId);

    Optional<DailyReport> findByKakaoIdAndDate(String kakaoId, LocalDate date);
}
