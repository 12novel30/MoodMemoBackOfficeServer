package com.moodmemo.office.repository;

import com.moodmemo.office.domain.Stamps;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

// create config for mongodb
// https://www.baeldung.com/spring-data-mongodb-tutorial


public interface StampRepository extends MongoRepository<Stamps, String> {

    // please create method find by kakaoId and between dateTime sort by dateTime
    List<Stamps> findByKakaoIdAndDateTimeBetweenOrderByDateTime(
            String kakaoId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

//    Optional<Stamps> findByKakaoIdAndLocalTimeAndLocalDate(String kakaoId,
//                                                           LocalTime localTime,
//                                                           LocalDate localDate);

    int countByKakaoIdAndDateTimeBetween(String kakaoId,
                                         LocalDateTime startDateTime,
                                         LocalDateTime endDateTime);

    // TODO - user 는 key 를 걸자 -> 얘로 속도 많이 빨라질 것!
    // 몽고디비는 시계열로 들어간다 -> user index 걸어보자
    // TODO - LocalDateTime 은 날짜로 캐스팅하는 필드 하나 만들어보자
    List<Stamps> findByKakaoIdAndDateTimeBetween(String kakaoId,
                                                 LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime);
}
