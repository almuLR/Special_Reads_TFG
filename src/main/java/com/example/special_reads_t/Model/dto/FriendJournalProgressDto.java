package com.example.special_reads_t.Model.dto;

import com.example.special_reads_t.Model.User;

public class FriendJournalProgressDto {

    private String friendName;
    private String bookTitle;
    private Integer progress;
    private Long id;
    private String profilePhotoUrl;

    public FriendJournalProgressDto(String friendName, String bookTitle, Integer progress) {
        this.friendName = friendName;
        this.bookTitle = bookTitle;
        this.progress = progress;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getFriendName() {
        return friendName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FriendJournalProgressDto() {}

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
