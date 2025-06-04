package com.example.special_reads_t.Service;

import com.example.special_reads_t.Model.League;
import com.example.special_reads_t.Model.LeagueType;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.LeagueDto;
import com.example.special_reads_t.Model.dto.RankingDto;
import com.example.special_reads_t.Repository.JournalRepository;
import com.example.special_reads_t.Repository.LeagueRepository;
import com.example.special_reads_t.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LeagueService {
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    public League createInternalLeague(String title, Integer bookGoal, List<User> participants) {
        League league = new League();
        league.setTitle(title);
        league.setLeagueType(LeagueType.INTERNAL);
        league.setBooksGoal(bookGoal);
        league.setParticipants(participants);
        league.setCreationDate(LocalDate.now());
        return leagueRepository.save(league);
    }

    public League updateInternalLeague(Long leagueId, String newTitle, Integer newBooksGoal, List<User> newParticipants) {
        Optional<League> optionalLeague = leagueRepository.findById(leagueId);
        if (optionalLeague.isPresent()) {
            League league = optionalLeague.get();
            if (league.getLeagueType().equals(LeagueType.INTERNAL)) {
                league.setTitle(newTitle);
                league.setBooksGoal(newBooksGoal);
                league.setParticipants(newParticipants);
                return leagueRepository.save(league);
            }
        }
        return null;
    }

    public Optional<League> getLeagueById(Long leagueId) {
        return leagueRepository.findById(leagueId);
    }

    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }

    public List<RankingDto> calculateRanking(League league){
        List<RankingDto> ranking = new ArrayList<>();
        for (User user : league.getParticipants()) {
            Long booksCount;
            if (league.getLeagueType() == LeagueType.INTERNAL) {
                booksCount = journalRepository.countByUserAndStatusAndFinishDateGreaterThanEqual(user, "Terminado", league.getCreationDate().atStartOfDay());
            } else {
                booksCount = journalRepository.countByUserAndStatus(user, "Terminado");
            }
            ranking.add(new RankingDto(user.getUsername(), booksCount));
        }

        ranking.sort(Comparator.comparingLong(r -> {
            if (league.getLeagueType() == LeagueType.INTERNAL) {
                return Math.abs(league.getBooksGoal().longValue() - r.getBooksCount());
            }
            return -r.getBooksCount();
        }));
        return ranking;
    }

    public List<League> getDefaultsLeagues(User currentUser) {
        List<League> defaultsLeagues = leagueRepository.findByLeagueType(LeagueType.DEFAULT);
        if (defaultsLeagues != null && defaultsLeagues.isEmpty()) {
            for (League league : defaultsLeagues) {
                if ("Liga Mundial".equals(league.getTitle())) {
                    List<User> allUsers = userService.getAllUsers();
                    league.setParticipants(allUsers);
                } else if ("Liga Nacional".equals(league.getTitle())) {
                    if (currentUser.getCountry().equals("España")) {
                        List<User> spanishUsers = userService.getUsersByCountry("España");
                        league.setParticipants(spanishUsers);
                    } else {
                        List<User> portuguesUsers = userService.getUsersByCountry("Portugal");
                        league.setParticipants(portuguesUsers);
                    }
                } else if ("Liga Ibérica".equals(league.getTitle())) {
                    List<User> interUsers = new ArrayList<>(userService.getUsersByCountry("España"));
                    interUsers.addAll(userService.getUsersByCountry("Portugal"));
                    league.setParticipants(interUsers);
                }
                leagueRepository.save(league);
            }
            return defaultsLeagues;
        }
            defaultsLeagues = new ArrayList<>();

            League mundial = new League();
            mundial.setTitle("Liga Mundial");
            mundial.setLeagueType(LeagueType.DEFAULT);
            mundial.setCreationDate(LocalDate.now());
            mundial.setBooksGoal(null);
            List<User> allUsers = userService.getAllUsers();
            mundial.setParticipants(allUsers);
            mundial = leagueRepository.save(mundial);
            defaultsLeagues.add(mundial);

            League nacional = new League();
            nacional.setTitle("Liga Nacional");
            nacional.setLeagueType(LeagueType.DEFAULT);
            nacional.setCreationDate(LocalDate.now());
            nacional.setBooksGoal(null);
            if (currentUser.getCountry().equals("España")) {
                List<User> spanishUsers = userService.getUsersByCountry("España");
                nacional.setParticipants(spanishUsers);
            } else {
                List<User> portuguesUsers = userService.getUsersByCountry("Portugal");
                nacional.setParticipants(portuguesUsers);
            }
            nacional = leagueRepository.save(nacional);
            defaultsLeagues.add(nacional);

            League iberica = new League();
            iberica.setTitle("Liga Ibérica");
            iberica.setLeagueType(LeagueType.DEFAULT);
            iberica.setCreationDate(LocalDate.now());
            iberica.setBooksGoal(null);
            List<User> interUsers = new ArrayList<>(userService.getUsersByCountry("España"));
            interUsers.addAll(userService.getUsersByCountry("Portugal"));
            iberica.setParticipants(interUsers);
            iberica = leagueRepository.save(iberica);
            defaultsLeagues.add(iberica);


        return defaultsLeagues;
    }

    public List<League> getInternalLeaguesForUser(User user) {
        return leagueRepository.findByLeagueTypeAndParticipantsContaining(LeagueType.INTERNAL, user);
    }

    public List<LeagueDto> getDefaultsLeaguesDtos(User currentUser) {
        List<League> defaultsLeagues = getDefaultsLeagues(currentUser);
        List<LeagueDto> defaultsLeaguesDtos = new ArrayList<>();
        for (League league : defaultsLeagues) {
            List<RankingDto> ranking = calculateRanking(league);
            int position = -1;
            for ( int i = 0; i < ranking.size(); i++ ) {
                RankingDto r = ranking.get(i);
                if (r.getUserName().equals(currentUser.getUsername())) {
                    position = i + 1;
                    break;
                }
            }
            if (position == -1) {
                position = ranking.size();
            }
            int totalReaders = league.getParticipants() != null ? league.getParticipants().size() : 0;
            defaultsLeaguesDtos.add(new LeagueDto(league.getId(), league.getTitle(), position, totalReaders));
        }
        return defaultsLeaguesDtos;
    }

    public List<LeagueDto> getInternalLeaguesDtos(User currentUser) {
        List<League> internalLeagues = getInternalLeaguesForUser(currentUser);
        List<LeagueDto> internalLeaguesDtos = new ArrayList<>();
        for (League league : internalLeagues) {
            List<RankingDto> ranking = calculateRanking(league);
            for (RankingDto r : ranking) {
                long diff = Math.abs(league.getBooksGoal() - r.getBooksCount());
                System.out.println("Usuario " + r.getUserName() + " - Libros: " + r.getBooksCount() + " - Dif: " + diff);
            }
            int position = 0;
            for ( int i = 0; i < ranking.size(); i++ ) {
                RankingDto r = ranking.get(i);
                if (r.getUserName().equals(currentUser.getUsername())) {
                    position = i + 1;
                    break;
                }
            }
            int totalReaders = league.getParticipants() != null ? league.getParticipants().size() : 0;
            internalLeaguesDtos.add(new LeagueDto(league.getId(), league.getTitle(), position, totalReaders));
        }
        return internalLeaguesDtos;
    }
}
