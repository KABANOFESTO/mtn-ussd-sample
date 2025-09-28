package com.ussd.mtn.repository;

import com.ussd.mtn.model.UssdSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UssdSessionRepository extends MongoRepository<UssdSession, String> {
    Optional<UssdSession> findBySessionIdAndIsActiveTrue(String sessionId);

    Optional<UssdSession> findByPhoneNumberAndIsActiveTrue(String phoneNumber);
}
