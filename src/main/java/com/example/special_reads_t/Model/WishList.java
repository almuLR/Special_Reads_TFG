package com.example.special_reads_t.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists")
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = true)
    private Book book;

    private LocalDateTime addedAt = LocalDateTime.now();
    @Column
    private String manualTitle;

    public WishList() {}

    public WishList(User user, Book book) {
        this.user = user;
        this.book = book;
        this.addedAt = LocalDateTime.now();
    }

    // getters & setters

    public String getManualTitle() {
        return manualTitle;
    }

    public void setManualTitle(String manualTitle) {
        this.manualTitle = manualTitle;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
