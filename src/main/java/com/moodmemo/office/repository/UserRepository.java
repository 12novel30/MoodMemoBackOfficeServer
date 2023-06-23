package com.moodmemo.office.repository;

import com.moodmemo.office.domain.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<Users, String> {
    Users findByKakaoId(String kakaoId);

    // please find method sorted by week1 limit 1
    List<Users> findTop1ByOrderByWeek1Desc();

    List<Users> findTop1ByOrderByWeek2Desc();

    List<Users> findTop1ByOrderByWeek3Desc();

    List<Users> findTop1ByOrderByWeek4Desc();

    //    find week1 by kakaoId
    Users findWeek1ByKakaoId(String kakaoId);

    Users findWeek2ByKakaoId(String kakaoId);

    Users findWeek3ByKakaoId(String kakaoId);

    Users findWeek4ByKakaoId(String kakaoId);

}
