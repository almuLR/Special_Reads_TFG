package com.example.special_reads_t.Service;


import com.example.special_reads_t.Model.Friend;
import com.example.special_reads_t.Model.JournalEntry;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Model.dto.FriendJournalProgressDto;
import com.example.special_reads_t.Repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    public List<FriendJournalProgressDto> getProgressReadingsFirends(User currentUser) {
        List<Friend> friendsRelations = friendRepository.findByOwner(currentUser);
        List<FriendJournalProgressDto> result = new ArrayList<>();

        for (Friend friendship : friendsRelations) {
            User friendUser = friendship.getFriend();
            if (friendUser.getJournalEntries() != null) {
               for (JournalEntry entry : friendUser.getJournalEntries()) {
                   if (entry.getBook() != null){
                       String bookTitle = entry.getBook().getTitle();
                       int progressValue = entry.getProgress();
                       FriendJournalProgressDto dto = new FriendJournalProgressDto(friendUser.getUsername(), bookTitle, progressValue);
                       result.add(dto);
                   }
               }
            }
        }
        return result;
    }

    public Optional<Friend> getRequest(User user1, User user2) {
        Optional<Friend> direct  = friendRepository.findByOwnerAndFriend(user1, user2)
                .filter(f -> !"RECHAZADA".equalsIgnoreCase(f.getStatus()));
        Optional<Friend> inverse = friendRepository.findByOwnerAndFriend(user2, user1)
                .filter(f -> !"RECHAZADA".equalsIgnoreCase(f.getStatus()));

        return direct.isPresent() ? direct : inverse;
    }


}
