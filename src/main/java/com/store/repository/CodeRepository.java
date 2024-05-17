package com.store.repository;

import com.store.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {
    Code findByValue(int value);

    Code findCodeByUserEmail(String email);

    Optional<Code> findCodeByUserEmailAndTypeAndValue(String email, String type, int value);

    void deleteByValue(int value);

    List<Code> findCodesByExpireAtBefore(ZonedDateTime now);
}
