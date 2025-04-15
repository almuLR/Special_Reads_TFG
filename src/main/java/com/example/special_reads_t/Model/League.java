package com.example.special_reads_t.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "LEAGUE")
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeagueType leagueType;

    private Integer booksGoal;

    private LocalDate creationDate;

    @ManyToMany
    @JoinTable(
            name = "league_users",
            joinColumns = @JoinColumn(name = "league_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    public League(Long id, String title, LeagueType leagueType, Integer booksGoal, LocalDate creationDate, List<User> participants) {
        this.id = id;
        this.title = title;
        this.leagueType = leagueType;
        this.booksGoal = booksGoal;
        this.creationDate = creationDate;
        this.participants = participants;
    }
    public League() {}

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

    public LeagueType getLeagueType() {
        return leagueType;
    }

    public void setLeagueType(LeagueType leagueType) {
        this.leagueType = leagueType;
    }

    public Integer getBooksGoal() {
        return booksGoal;
    }

    public void setBooksGoal(Integer booksGoal) {
        this.booksGoal = booksGoal;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}
