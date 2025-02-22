package com.example.special_reads_t.Model.dto;

import com.example.special_reads_t.Model.Book;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksResponse {

    private int totalResults;

    @JsonProperty("items")
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
