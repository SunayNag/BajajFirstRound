package com.example.hiring.repo;

import com.example.hiring.model.entity.SubmissionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRecordRepository extends JpaRepository<SubmissionRecord, Long> {
}
