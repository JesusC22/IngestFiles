package com.paid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "call_detail_records")
public class CallDetailRecordsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "call_detail_records_seq")
    @SequenceGenerator(name = "call_detail_records_seq", sequenceName = "call_detail_records_seq", allocationSize = 1)
    @Column(name = "id_call_detail_records")
    private Long idCallDetailRecords;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "record_line")
    private String recordLine;

    public Long getIdCallDetailRecords() {
        return idCallDetailRecords;
    }

    public void setIdCallDetailRecords(Long idCallDetailRecords) {
        this.idCallDetailRecords = idCallDetailRecords;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRecordLine() {
        return recordLine;
    }

    public void setRecordLine(String recordLine) {
        this.recordLine = recordLine;
    }
}
