package com.paid.repository;

import com.paid.entity.CallDetailRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallDetailRecordsRepository extends JpaRepository<CallDetailRecordsEntity,Long> {
}
