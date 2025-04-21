package com.example.special_reads_t.Unit;

import com.example.special_reads_t.Controllers.REST.FriendRestController;
import com.example.special_reads_t.Model.Friend;
import com.example.special_reads_t.Model.dto.FriendJournalProgressDto;
import com.example.special_reads_t.Model.User;
import com.example.special_reads_t.Repository.FriendRepository;
import com.example.special_reads_t.Security.jwt.JwtTokenProvider;
import com.example.special_reads_t.Service.FriendService;
import com.example.special_reads_t.Service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class FriendRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FriendService friendService;

    @MockBean
    private FriendRepository friendRepository;

    // Mock security dependencies to disable JWT filter
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("POST /api/friends/request - success")
    @Test
    void sendFriendRequest_Success() throws Exception {
        User alice = new User();
        alice.setId(1L);
        alice.setUsername("alice");

        User bob = new User();
        bob.setId(2L);
        bob.setUsername("bob");

        when(userService.getCurrentUser()).thenReturn(alice);
        when(userService.findById(2L)).thenReturn(Optional.of(bob));
        when(friendService.getRequest(alice, bob)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/friends/request")
                        .param("friendId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud de amistad enviada"));

        verify(friendRepository, times(1)).save(any(Friend.class));
    }

    @DisplayName("GET /api/friends/requests - unauthorized")
    @Test
    void getReceivedFriendRequests_Unauthorized() throws Exception {
        when(userService.getCurrentUser()).thenReturn(null);

        mockMvc.perform(get("/api/friends/requests"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("POST /api/friends/accept - forbidden when not recipient")
    @Test
    void acceptRequest_Forbidden() throws Exception {
        User alice = new User(); alice.setId(1L);
        User other = new User(); other.setId(3L);
        Friend request = new Friend();
        request.setId(5L);
        request.setFriend(other);

        when(userService.getCurrentUser()).thenReturn(alice);
        when(friendRepository.findById(5L)).thenReturn(Optional.of(request));

        mockMvc.perform(post("/api/friends/accept").param("friendId", "5"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("GET /api/friends/accept - returns reading progress")
    @Test
    void getAcceptedFriends_ReturnsProgress() throws Exception {
        User alice = new User(); alice.setId(1L);
        User bob = new User(); bob.setId(2L);
        bob.setUsername("bob");
        Friend f = new Friend(); f.setId(7L);
        f.setOwner(alice); f.setFriend(bob); f.setStatus("ACEPTADA");

        FriendJournalProgressDto dto = new FriendJournalProgressDto();
        dto.setId(7L);
        dto.setFriendName("bob");
        dto.setBookTitle("Some Book");
        dto.setProgress(50);
        dto.setProfilePhotoUrl("url.jpg");

        when(userService.getCurrentUser()).thenReturn(alice);
        when(friendRepository.findAcceptedFriendsForUser(alice)).thenReturn(List.of(f));

        mockMvc.perform(get("/api/friends/accept"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
