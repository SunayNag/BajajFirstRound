package com.example.hiring.runner;

import com.example.hiring.model.dto.GenerateWebhookRequest;
import com.example.hiring.model.dto.GenerateWebhookResponse;
import com.example.hiring.model.entity.SubmissionRecord;
import com.example.hiring.repo.SubmissionRecordRepository;
import com.example.hiring.service.HiringClient;
import com.example.hiring.service.SqlSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final HiringClient hiringClient;
    private final SqlSolver sqlSolver;
    private final SubmissionRecordRepository repo;

    @Value("${app.applicant.name}")
    private String name;

    @Value("${app.applicant.regNo}")
    private String regNo;

    @Value("${app.applicant.email}")
    private String email;

    public StartupRunner(HiringClient hiringClient, SqlSolver sqlSolver, SubmissionRecordRepository repo) {
        this.hiringClient = hiringClient;
        this.sqlSolver = sqlSolver;
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        log.info("Starting hiring submission flow...");

        // 1) Generate webhook and token
        GenerateWebhookRequest req = new GenerateWebhookRequest(name, regNo, email);
        GenerateWebhookResponse resp = hiringClient.generateWebhook(req);

        // 2) Decide question and resolve SQL
        String question = sqlSolver.currentQuestionLabel();
        String finalSql = sqlSolver.resolveFinalSql();
        log.info("Determined question: {}. Final SQL size: {} bytes", question, finalSql.length());

        // 3) Store as PENDING
        SubmissionRecord record = new SubmissionRecord();
        record.setRegNo(regNo);
        record.setQuestion(question);
        record.setFinalQuery(finalSql);
        record.setWebhookUrl(resp.getWebhook());
        record.setSubmitStatus("PENDING");
        record = repo.save(record);

        // 4) Submit with JWT in Authorization header
        try {
            ResponseEntity<String> submitResp = hiringClient.submitFinalQuery(resp.getWebhook(), resp.getAccessToken(), finalSql);
            if (submitResp.getStatusCode().is2xxSuccessful()) {
                record.setSubmitStatus("SUCCESS");
                log.info("Submission SUCCESS. Response: {}", submitResp.getBody());
            } else {
                record.setSubmitStatus("FAILED");
                String body = submitResp.getBody();
                record.setErrorMessage("HTTP " + submitResp.getStatusCode() + (body != null ? (": " + body) : ""));
                log.error("Submission FAILED: {}", record.getErrorMessage());
            }
        } catch (Exception ex) {
            record.setSubmitStatus("FAILED");
            record.setErrorMessage(ex.getMessage());
            log.error("Submission error", ex);
        }

        repo.save(record);
        log.info("Flow complete. Record id={}", record.getId());
    }
}
