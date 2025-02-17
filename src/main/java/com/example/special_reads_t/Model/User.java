package com.example.special_reads_t.Model;


import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String email;

    private Date dateOfBirth;

    private String encodedPassword;

    @ElementCollection (fetch = FetchType.EAGER)
    private List<String> roles;

    @ElementCollection (fetch = FetchType.EAGER)
    private List<String> favoriteGenres;

    @ManyToMany
    @JoinTable(
            name = "USER_FRIENDS",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BookProgress> bookProgresses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews;

}
