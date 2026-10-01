package com.example.demo;

import com.example.demo.controller.TweetController;
import com.example.demo.dto.TweetRequest;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ApiException;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.service.TweetService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TweetController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TweetService tweetService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Tweet tweet;
    private TweetRequest tweetRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ali");
        user.setSurname("Yilmaz");
        user.setEmail("ali@example.com");
        user.setPassword("secret");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Ornek tweet icerigi");
        tweet.setUser(user);

        tweetRequest = new TweetRequest();
        tweetRequest.setContent("Ornek tweet icerigi");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    @DisplayName("Tüm tweetleri getir")
    void findAll() throws Exception {
        given(tweetService.findAll()).willReturn(List.of(tweet));

        mockMvc.perform(get("/tweet/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Ornek tweet icerigi")));
    }

    @Test
    @DisplayName("ID ile tweet getir")
    void findById() throws Exception {
        given(tweetService.findById(1L)).willReturn(tweet);

        mockMvc.perform(get("/tweet/findById").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Ornek tweet icerigi")));
    }

    @Test
    @DisplayName("Kullanıcı ID'sine göre tweetleri getir")
    void findAllByUserId() throws Exception {
        given(tweetService.findAllByUserId(1L)).willReturn(List.of(tweet));

        mockMvc.perform(get("/tweet/findByUserId").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("Yeni tweet kaydet")
    void save() throws Exception {
        given(tweetService.save(any(Tweet.class))).willReturn(tweet);

        mockMvc.perform(post("/tweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tweetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Ornek tweet icerigi")));

        verify(tweetService).save(any(Tweet.class));
    }

    @Test
    @DisplayName("Retweet kaydet")
    void saveRetweet() throws Exception {
        TweetRequest retweetRequest = new TweetRequest();
        retweetRequest.setTweetId(1L);

        Tweet retweet = new Tweet();
        retweet.setId(2L);
        retweet.setUser(user);
        retweet.setParentTweet(tweet);

        given(tweetService.findById(1L)).willReturn(tweet);
        given(tweetService.save(any(Tweet.class))).willReturn(retweet);

        mockMvc.perform(post("/tweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retweetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)));
    }

    @Test
    @DisplayName("Tweet güncelle")
    void update() throws Exception {
        Tweet updatedTweet = new Tweet();
        updatedTweet.setId(1L);
        updatedTweet.setContent("Guncellenmis icerik");
        updatedTweet.setUser(user);

        given(tweetService.update(eq(1L), eq(1L), any(Tweet.class))).willReturn(updatedTweet);

        mockMvc.perform(put("/tweet/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tweetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Guncellenmis icerik")));
    }

    @Test
    @DisplayName("Tweet sil")
    void deleteTweet() throws Exception {
        mockMvc.perform(delete("/tweet/{id}", 1L))
                .andExpect(status().isOk());

        verify(tweetService).delete(1L, 1L);
    }

    @Test
    @DisplayName("ApiException yakalanmalı")
    void testHandleApiException() throws Exception {
        when(tweetService.findById(1L))
                .thenThrow(new ApiException("Tweet bulunamadi", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/tweet/findById").param("id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tweet bulunamadi"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Genel exception yakalanmalı")
    void testHandleGeneralException() throws Exception {
        when(tweetService.findById(1L))
                .thenThrow(new RuntimeException("Beklenmeyen hata"));

        mockMvc.perform(get("/tweet/findById").param("id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Beklenmeyen hata"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("Geçersiz tweet isteği başarılı yanıt dönmemeli")
    void saveWithInvalidRequestShouldNotSucceed() throws Exception {
        TweetRequest invalidRequest = new TweetRequest();

        mockMvc.perform(post("/tweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is5xxServerError());
    }
}
