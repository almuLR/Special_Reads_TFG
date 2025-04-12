package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.Friend;
import com.example.special_reads_t.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByOwner(User owner);

    @Query ("SELECT f FROM Friend f WHERE f.friend =:friendUser AND f.status = :status")
    List<Friend> findByFriendAndStatus(@Param("friendUser") User friendUser, @Param("status") String status);

    Optional<Friend> findByOwnerAndFriend(User owner, User friend);

    @Query("SELECT f FROM Friend f " +
            "WHERE (f.owner = :user OR f.friend = :user) AND f.status = 'ACEPTADA'")
    List<Friend> findAcceptedFriendsForUser(@Param("user") User user);

}
