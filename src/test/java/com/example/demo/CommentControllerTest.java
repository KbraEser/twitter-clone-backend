package com.example.demo;

import com.example.demo.controller.CommentController;
import com.example.demo.dto.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.service.CommentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private TweetService tweetService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Tweet tweet;
    private Comment comment;
    private CommentRequest commentRequest;

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
        tweet.setContent("Tweet icerigi");
        tweet.setUser(user);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Guzel paylasim!");
        comment.setUser(user);
        comment.setTweet(tweet);

        commentRequest = new CommentRequest();
        commentRequest.setContent("Guzel paylasim!");
        commentRequest.setTweetId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    @DisplayName("Tweet ID'sine göre yorumları getir")
    void findAllByTweetId() throws Exception {
        given(commentService.findAllByTweetId(1L)).willReturn(List.of(comment));

        mockMvc.perform(get("/comment/findByTweetId").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Guzel paylasim!")));
    }

    @Test
    @DisplayName("Yeni yorum kaydet")
    void save() throws Exception {
        given(tweetService.findById(1L)).willReturn(tweet);
        given(commentService.save(any(Comment.class))).willReturn(comment);

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Guzel paylasim!")));

        verify(commentService).save(any(Comment.class));
    }

    @Test
    @DisplayName("Yorum güncelle")
    void update() throws Exception {
        Comment updated = new Comment();
        updated.setId(1L);
        updated.setContent("Guncellenmis yorum");

        given(commentService.update(eq(1L), eq(1L), any(Comment.class))).willReturn(updated);

        mockMvc.perform(put("/comment/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Guncellenmis yorum")));
    }

    @Test
    @DisplayName("Yorum sil")
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/comment/{id}", 1L))
                .andExpect(status().isOk());

        verify(commentService).delete(1L, 1L);
    }

    @Test
    @DisplayName("Boş içerikli yorum başarılı yanıt dönmemeli")
    void saveWithBlankContentShouldNotSucceed() throws Exception {
        CommentRequest invalidRequest = new CommentRequest();
        invalidRequest.setContent("");
        invalidRequest.setTweetId(1L);

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is5xxServerError());
    }
}
