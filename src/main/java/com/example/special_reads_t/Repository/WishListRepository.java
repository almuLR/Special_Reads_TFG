package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByUser(User user);

    void deleteByUserAndBookId(User user, Long bookId);
}
