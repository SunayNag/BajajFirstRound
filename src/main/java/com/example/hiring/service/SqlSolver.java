package com.example.hiring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
public class SqlSolver {

    @Value("${app.applicant.regNo}")
    private String regNo;

    @Value("${app.sql.question1:}")
    private String sqlQ1FromConfig;

    @Value("${app.sql.question2:}")
    private String sqlQ2FromConfig;

    public boolean isOddQuestion() {
        String digits = regNo.replaceAll("\\D", "");
        if (digits.length() < 2) {
            throw new IllegalArgumentException("regNo must contain at least two digits: " + regNo);
        }
        int lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
        return lastTwo % 2 == 1;
    }

    public String currentQuestionLabel() {
        return isOddQuestion() ? "Q1" : "Q2";
    }

    public String resolveFinalSql() {
        boolean odd = isOddQuestion();

        String fromConfig = odd ? sqlQ1FromConfig : sqlQ2FromConfig;
        if (fromConfig != null && !fromConfig.isBlank()) {
            return normalize(fromConfig);
        }

        String path = odd ? "sql/question1.sql" : "sql/question2.sql";
        try {
            ClassPathResource res = new ClassPathResource(path);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
                String content = br.lines().collect(Collectors.joining("\n"));
                return normalize(content);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load " + path + ". Put your final SQL there.", e);
        }
    }

    private String normalize(String sql) {
        return sql == null ? "" : sql.trim();
    }
}
