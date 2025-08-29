package com.example.hiring.model.dto;

public class SubmitFinalQueryRequest {
    private String finalQuery;

    public SubmitFinalQueryRequest() {}
    public SubmitFinalQueryRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() { return finalQuery; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
}
