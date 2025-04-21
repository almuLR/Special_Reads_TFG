package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Model.League;
import com.example.special_reads_t.Model.LeagueType;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.RankingDto;
import com.example.special_reads_t.Repository.JournalRepository;
import com.example.special_reads_t.Repository.LeagueRepository;
import com.example.special_reads_t.Service.LeagueService;
import com.example.special_reads_t.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private JournalRepository journalRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private LeagueService leagueService;

    private User user1, user2;
    private League internalLeague;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("alice");
        user1.setCountry("España");

        user2 = new User();
        user2.setUsername("bob");
        user2.setCountry("Portugal");

        // Internal league setup
        internalLeague = new League();
        internalLeague.setId(1L);
        internalLeague.setLeagueType(LeagueType.INTERNAL);
        internalLeague.setBooksGoal(5);
        internalLeague.setCreationDate(LocalDate.of(2025, 1, 1));
        internalLeague.setParticipants(Arrays.asList(user1, user2));
    }

    @Test
    void createInternalLeague_savesWithInternalType() {
        when(leagueRepository.save(any(League.class))).thenAnswer(inv -> inv.getArgument(0));

        League result = leagueService.createInternalLeague("Liga Prueba", 10, List.of(user1));

        assertThat(result.getTitle()).isEqualTo("Liga Prueba");
        assertThat(result.getLeagueType()).isEqualTo(LeagueType.INTERNAL);
        assertThat(result.getBooksGoal()).isEqualTo(10);
        assertThat(result.getParticipants()).containsExactly(user1);
        assertThat(result.getCreationDate()).isEqualTo(LocalDate.now());
        verify(leagueRepository).save(result);
    }

    @Test
    void updateInternalLeague_existingInternal_updatesAndSaves() {
        when(leagueRepository.findById(1L)).thenReturn(Optional.of(internalLeague));
        when(leagueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        League updated = leagueService.updateInternalLeague(1L, "Nueva", 8, Collections.singletonList(user2));

        assertThat(updated.getTitle()).isEqualTo("Nueva");
        assertThat(updated.getBooksGoal()).isEqualTo(8);
        assertThat(updated.getParticipants()).containsExactly(user2);
    }

    @Test
    void updateInternalLeague_notInternal_returnsNull() {
        League ext = new League();
        ext.setId(2L);
        ext.setLeagueType(LeagueType.DEFAULT);
        when(leagueRepository.findById(2L)).thenReturn(Optional.of(ext));

        League result = leagueService.updateInternalLeague(2L, "X", 1, List.of(user1));
        assertThat(result).isNull();
    }

    @Test
    void calculateRanking_internal_leagueOrdersByGoalDifference() {
        internalLeague.setBooksGoal(3);
        internalLeague.setLeagueType(LeagueType.INTERNAL);
        // user1 has 2, user2 has 5 since creation date
        when(journalRepository.countByUserAndStatusAndFinishDateGreaterThanEqual(
                eq(user1), eq("Terminado"), any(LocalDateTime.class))).thenReturn(2L);
        when(journalRepository.countByUserAndStatusAndFinishDateGreaterThanEqual(
                eq(user2), eq("Terminado"), any(LocalDateTime.class))).thenReturn(5L);

        List<RankingDto> ranking = leagueService.calculateRanking(internalLeague);

        // Differences: user1 -> |3-2|=1, user2 -> |3-5|=2 => user1 first
        assertThat(ranking.get(0).getUserName()).isEqualTo("alice");
        assertThat(ranking.get(1).getUserName()).isEqualTo("bob");
    }

    @Test
    void calculateRanking_default_leagueOrdersByCountDescending() {
        internalLeague.setLeagueType(LeagueType.DEFAULT);
        when(journalRepository.countByUserAndStatus(user1, "Terminado")).thenReturn(4L);
        when(journalRepository.countByUserAndStatus(user2, "Terminado")).thenReturn(1L);

        List<RankingDto> ranking = leagueService.calculateRanking(internalLeague);

        assertThat(ranking.get(0).getUserName()).isEqualTo("alice");
        assertThat(ranking.get(1).getUserName()).isEqualTo("bob");
    }

}
