package com.paid.repository;

import com.paid.entity.CdrLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CdrLogsRepository extends JpaRepository<CdrLogsEntity, Long> {

    @Transactional(readOnly = true)
    CdrLogsEntity findByFileName(String finalname);
}
