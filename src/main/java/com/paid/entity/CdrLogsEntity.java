package com.paid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.OffsetTime;

@Entity
@Table(name = "cdr_logs")
public class CdrLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cdr_logs_seq")
    @SequenceGenerator(name = "cdr_logs_seq", sequenceName = "cdr_logs_seq", allocationSize = 1)
    @Column(name = "id_cdr_logs")
    private Long idCdrLogs;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "upload_start_time")
    private OffsetTime uploadStartTime;

    @Column(name = "upload_end_time")
    private OffsetTime uploadEndTime;

    @Column(name = "number_of_successfully_loaded_records")
    private Long numberOfSuccessfullyLoadedRecords;

    @Column(name = "number_of_failed_records")
    private Long numberOfFailedRecords;

    public Long getIdCdrLogs() {
        return idCdrLogs;
    }

    public void setIdCdrLogs(Long idCdrLogs) {
        this.idCdrLogs = idCdrLogs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public OffsetTime getUploadStartTime() {
        return uploadStartTime;
    }

    public void setUploadStartTime(OffsetTime uploadStartTime) {
        this.uploadStartTime = uploadStartTime;
    }

    public OffsetTime getUploadEndTime() {
        return uploadEndTime;
    }

    public void setUploadEndTime(OffsetTime uploadEndTime) {
        this.uploadEndTime = uploadEndTime;
    }

    public Long getNumberOfSuccessfullyLoadedRecords() {
        return numberOfSuccessfullyLoadedRecords;
    }

    public void setNumberOfSuccessfullyLoadedRecords(Long numberOfSuccessfullyLoadedRecords) {
        this.numberOfSuccessfullyLoadedRecords = numberOfSuccessfullyLoadedRecords;
    }

    public Long getNumberOfFailedRecords() {
        return numberOfFailedRecords;
    }

    public void setNumberOfFailedRecords(Long numberOfFailedRecords) {
        this.numberOfFailedRecords = numberOfFailedRecords;
    }
}
