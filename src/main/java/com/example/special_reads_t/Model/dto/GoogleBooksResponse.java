package com.example.special_reads_t.Model.dto;

import com.example.special_reads_t.Model.Book;

import java.util.List;

public class GoogleBooksResponse {

    private int totalResults;
    private List<GoogleBookResult> results;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<GoogleBookResult> getResults() {
        return results;
    }

    public void setResults(List<GoogleBookResult> results) {
        this.results = results;
    }
}
