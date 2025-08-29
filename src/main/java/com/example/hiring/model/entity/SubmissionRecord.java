package com.example.hiring.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "submission_records")
public class SubmissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regNo;
    private String question; // "Q1" or "Q2"

    @Column(length = 4000)
    private String finalQuery;

    private String webhookUrl;
    private String submitStatus; // "PENDING", "SUCCESS", "FAILED"

    @Column(length = 2000)
    private String errorMessage;

    @CreationTimestamp
    private Instant createdAt;

    public SubmissionRecord() {}

    public Long getId() { return id; }
    public String getRegNo() { return regNo; }
    public String getQuestion() { return question; }
    public String getFinalQuery() { return finalQuery; }
    public String getWebhookUrl() { return webhookUrl; }
    public String getSubmitStatus() { return submitStatus; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public void setQuestion(String question) { this.question = question; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    public void setSubmitStatus(String submitStatus) { this.submitStatus = submitStatus; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
