package com.moodmemo.office.repository;

import com.moodmemo.office.domain.DailyReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyReportRepository extends MongoRepository<DailyReport, String> {
    DailyReport findByKakaoId(String kakaoId);
}
