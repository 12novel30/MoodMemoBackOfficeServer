package com.moodmemo.office.repository;

import com.moodmemo.office.domain.Stamps;
import org.springframework.data.mongodb.repository.MongoRepository;

// create config for mongodb
// https://www.baeldung.com/spring-data-mongodb-tutorial


public interface StampRepository extends MongoRepository<Stamps, String> {
}
