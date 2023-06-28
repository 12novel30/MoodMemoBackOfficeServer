package com.moodmemo.office.repository;

import com.moodmemo.office.domain.DailyReport;
import com.moodmemo.office.domain.Stamps;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.sql.Timestamp;
import java.util.List;

public interface DailyReportRepository extends MongoRepository<DailyReport, String> {
    DailyReport findByKakaoId(String kakaoId);

    DailyReport findByKakaoIdAndDateTimeBetweenOrderByDateTime(String kakaoId, Timestamp startDateTime, Timestamp endDateTime);
}
