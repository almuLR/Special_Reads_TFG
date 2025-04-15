package com.example.special_reads_t.Model.dto;

public class LeagueDto {
    private Long id;
    private String title;
    private int position;
    private int totalParticipants;

    public LeagueDto() {}

    public LeagueDto(Long id, String title, int position, int totalParticipants) {
        this.id = id;
        this.title = title;
        this.position = position;
        this.totalParticipants = totalParticipants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }
}
