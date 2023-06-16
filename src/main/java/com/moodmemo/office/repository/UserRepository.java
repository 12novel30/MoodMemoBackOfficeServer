package com.moodmemo.office.repository;

import com.moodmemo.office.domain.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Users, String> {
    Users findByKakaoId(String kakaoId);

}
