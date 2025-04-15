package com.example.special_reads_t.Repository;

import com.example.special_reads_t.Model.League;
import com.example.special_reads_t.Model.LeagueType;
import com.example.special_reads_t.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    List<League> findByLeagueType(LeagueType leagueType);

    List<League> findByLeagueTypeAndParticipantsContaining(LeagueType leagueType, User user);


}
