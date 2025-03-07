package com.example.special_reads_t.Model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleBookResult {
    private VolumeInfo volumeInfo;

    @JsonProperty("id")
    private String googleBookId;

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    public String getGoogleBookId() {
        return googleBookId;
    }

    public void setGoogleBookId(String googleBookId) {
        this.googleBookId = googleBookId;
    }
}
