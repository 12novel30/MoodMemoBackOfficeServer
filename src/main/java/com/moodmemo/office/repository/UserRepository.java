package com.moodmemo.office.repository;

import com.moodmemo.office.domain.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<Users, String> {

    List<Users> findAllByOrderByUsername();

    Optional<Users> findByKakaoId(String kakaoId);
    // please count method by kakaoid and between dateTime

    // please find method sorted by week1 limit 1
    List<Users> findTop1ByOrderByWeek1Desc();

    List<Users> findTop1ByOrderByWeek2Desc();

    List<Users> findTop1ByOrderByWeek3Desc();

    List<Users> findTop1ByOrderByWeek4Desc();

    List<Users> findAllByOrderByWeek1Desc();

    List<Users> findAllByOrderByWeek2Desc();

    List<Users> findAllByOrderByWeek3Desc();

    List<Users> findAllByOrderByWeek4Desc();

    // create find method by username
    Optional<Users> findByUsername(String username);
}
