package com.example.demo;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Like;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ApiException;
import com.example.demo.exceptions.ExceptionResponse;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.TweetRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CommentServiceImpl;
import com.example.demo.service.LikeServiceImpl;
import com.example.demo.service.TweetServiceImpl;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.util.TwitterValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Mock
    private TweetRepository mockTweetRepository;

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private LikeRepository mockLikeRepository;

    @Mock
    private UserRepository mockUserRepository;

    private User user;
    private User otherUser;
    private Tweet tweet;
    private Comment comment;
    private Like like;

    private TweetServiceImpl tweetService;
    private CommentServiceImpl commentService;
    private LikeServiceImpl likeService;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        user = createUser(1L, "Ali", "Yilmaz", "ali@example.com");
        otherUser = createUser(2L, "Ayse", "Demir", "ayse@example.com");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Merhaba Twitter!");
        tweet.setUser(user);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Harika tweet!");
        comment.setUser(user);
        comment.setTweet(tweet);

        like = new Like();
        like.setId(1L);
        like.setUser(user);
        like.setTweet(tweet);

        tweetService = new TweetServiceImpl(mockTweetRepository);
        commentService = new CommentServiceImpl(mockCommentRepository);
        likeService = new LikeServiceImpl(mockLikeRepository);
        userService = new UserServiceImpl(mockUserRepository);
    }

    private User createUser(Long id, String name, String surname, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setPassword("secret");
        return u;
    }

    // --- Entity ---

    @Test
    @DisplayName("Tweet getter ve setter değerleri doğru dönmeli")
    void testTweetSettersAndGetters() {
        assertEquals(1L, tweet.getId());
        assertEquals("Merhaba Twitter!", tweet.getContent());
        assertEquals(user, tweet.getUser());
    }

    @Test
    @DisplayName("Comment getter ve setter değerleri doğru dönmeli")
    void testCommentSettersAndGetters() {
        assertEquals(1L, comment.getId());
        assertEquals("Harika tweet!", comment.getContent());
        assertEquals(user, comment.getUser());
        assertEquals(tweet, comment.getTweet());
    }

    @Test
    @DisplayName("User getter ve setter değerleri doğru dönmeli")
    void testUserSettersAndGetters() {
        assertEquals(1L, user.getId());
        assertEquals("Ali", user.getName());
        assertEquals("Yilmaz", user.getSurname());
        assertEquals("ali@example.com", user.getEmail());
        assertEquals("ali@example.com", user.getUsername());
    }

    // --- Exception ---

    @Test
    @DisplayName("ApiException özel constructor mesaj ve status tutmalı")
    void testApiExceptionCustomConstructor() {
        ApiException exception = new ApiException("Hata mesaji", HttpStatus.BAD_REQUEST);

        assertEquals("Hata mesaji", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    @DisplayName("ApiException RuntimeException olmalı")
    void ensureApiExceptionIsARuntimeException() {
        ApiException exception = new ApiException("Hata", HttpStatus.INTERNAL_SERVER_ERROR);

        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(RuntimeException.class)
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("ExceptionResponse alanları doğru başlatılmalı")
    void testExceptionResponsePropertiesInitialization() {
        LocalDateTime now = LocalDateTime.now();
        ExceptionResponse response = new ExceptionResponse("Hata olustu", 400, now);

        assertEquals("Hata olustu", response.getMessage());
        assertEquals(400, response.getStatus());
        assertEquals(now, response.getDateTime());
    }

    // --- Validation ---

    @Test
    @DisplayName("Geçersiz ID için ApiException fırlatılmalı")
    void validateIdShouldThrowForInvalidId() {
        assertThatThrownBy(() -> TwitterValidation.validateId(null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid ID provided")
                .matches(e -> ((ApiException) e).getHttpStatus() == HttpStatus.BAD_REQUEST);

        assertThatThrownBy(() -> TwitterValidation.validateId(0L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid ID provided");
    }

    @Test
    @DisplayName("Geçerli ID validation hatası vermemeli")
    void validateIdShouldPassForValidId() {
        TwitterValidation.validateId(1L);
    }

    // --- Repository tip kontrolü ---

    @Test
    @DisplayName("TweetRepository JpaRepository örneği olmalı")
    void tweetRepositoryInstanceCheck() {
        assertTrue(mockTweetRepository instanceof JpaRepository);
    }

    @Test
    @DisplayName("CommentRepository JpaRepository örneği olmalı")
    void commentRepositoryInstanceCheck() {
        assertTrue(mockCommentRepository instanceof JpaRepository);
    }

    // --- TweetService ---

    @Test
    @DisplayName("Tüm tweetleri getir")
    void findAllTweets() {
        when(mockTweetRepository.findAllByOrderByIdDesc()).thenReturn(List.of(tweet));

        List<Tweet> tweets = tweetService.findAll();

        assertThat(tweets).hasSize(1).contains(tweet);
    }

    @Test
    @DisplayName("ID ile tweet bul - başarılı")
    void findTweetByIdSuccess() {
        when(mockTweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        Tweet found = tweetService.findById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("ID ile tweet bul - bulunamadı")
    void findTweetByIdNotFound() {
        when(mockTweetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tweetService.findById(99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Tweet not found with ID: 99")
                .matches(e -> ((ApiException) e).getHttpStatus() == HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Kullanıcı ID'sine göre tweetleri getir")
    void findAllByUserId() {
        when(mockTweetRepository.findByUserId(1L)).thenReturn(Arrays.asList(tweet));

        List<Tweet> tweets = tweetService.findAllByUserId(1L);

        assertThat(tweets).hasSize(1);
        verify(mockTweetRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Tweet kaydet")
    void saveTweet() {
        when(mockTweetRepository.save(any(Tweet.class))).thenReturn(tweet);

        Tweet saved = tweetService.save(tweet);

        assertThat(saved).isNotNull();
        verify(mockTweetRepository).save(tweet);
    }

    @Test
    @DisplayName("Tweet güncelle - başarılı")
    void updateTweetSuccess() {
        Tweet updatedData = new Tweet();
        updatedData.setContent("Guncellenmis icerik");

        when(mockTweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(mockTweetRepository.save(any(Tweet.class))).thenReturn(tweet);

        Tweet result = tweetService.update(1L, 1L, updatedData);

        assertThat(result.getContent()).isEqualTo("Guncellenmis icerik");
        verify(mockTweetRepository).save(tweet);
    }

    @Test
    @DisplayName("Tweet güncelle - başkasının tweeti")
    void updateTweetForbidden() {
        when(mockTweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        Tweet updatedData = new Tweet();
        updatedData.setContent("Yetkisiz guncelleme");

        assertThatThrownBy(() -> tweetService.update(1L, 2L, updatedData))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You can only update your own tweet.")
                .matches(e -> ((ApiException) e).getHttpStatus() == HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Tweet sil - başarılı")
    void deleteTweetSuccess() {
        when(mockTweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        doNothing().when(mockTweetRepository).delete(tweet);

        tweetService.delete(1L, 1L);

        verify(mockTweetRepository).delete(tweet);
    }

    @Test
    @DisplayName("Tweet sil - başkasının tweeti")
    void deleteTweetForbidden() {
        when(mockTweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        assertThatThrownBy(() -> tweetService.delete(1L, 2L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You can only delete your own tweet.")
                .matches(e -> ((ApiException) e).getHttpStatus() == HttpStatus.FORBIDDEN);
    }

    // --- CommentService ---

    @Test
    @DisplayName("Tweet ID'sine göre yorumları getir")
    void findAllCommentsByTweetId() {
        when(mockCommentRepository.findByTweetId(1L)).thenReturn(List.of(comment));

        List<Comment> comments = commentService.findAllByTweetId(1L);

        assertThat(comments).hasSize(1).contains(comment);
    }

    @Test
    @DisplayName("Yorum bul - bulunamadı")
    void findCommentByIdNotFound() {
        when(mockCommentRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.findById(5L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Comment not found with ID: 5");
    }

    @Test
    @DisplayName("Yorum güncelle - başkasının yorumu")
    void updateCommentForbidden() {
        when(mockCommentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment updated = new Comment();
        updated.setContent("Yetkisiz");

        assertThatThrownBy(() -> commentService.update(1L, 2L, updated))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You can only update your own comment.");
    }

    @Test
    @DisplayName("Yorum sil - başarılı")
    void deleteCommentSuccess() {
        when(mockCommentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(mockCommentRepository).delete(comment);

        commentService.delete(1L, 1L);

        verify(mockCommentRepository).delete(comment);
    }

    // --- LikeService ---

    @Test
    @DisplayName("Tweet beğen - başarılı")
    void saveTweetLikeSuccess() {
        when(mockLikeRepository.findByUserIdAndTweetId(1L, 1L)).thenReturn(Optional.empty());
        when(mockLikeRepository.save(any(Like.class))).thenReturn(like);

        Like saved = likeService.save(like);

        assertThat(saved).isNotNull();
        verify(mockLikeRepository).save(like);
    }

    @Test
    @DisplayName("Aynı tweet tekrar beğenilemez")
    void saveDuplicateTweetLike() {
        when(mockLikeRepository.findByUserIdAndTweetId(1L, 1L)).thenReturn(Optional.of(like));

        assertThatThrownBy(() -> likeService.save(like))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You have already liked this!")
                .matches(e -> ((ApiException) e).getHttpStatus() == HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Tweet beğenisini kaldır - başarılı")
    void deleteTweetLikeSuccess() {
        when(mockLikeRepository.findByUserIdAndTweetId(1L, 1L)).thenReturn(Optional.of(like));
        doNothing().when(mockLikeRepository).delete(like);

        likeService.deleteTweetLike(1L, 1L);

        verify(mockLikeRepository).delete(like);
    }

    @Test
    @DisplayName("Tweet beğenisini kaldır - bulunamadı")
    void deleteTweetLikeNotFound() {
        when(mockLikeRepository.findByUserIdAndTweetId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.deleteTweetLike(1L, 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Like not found for this tweet!");
    }

    // --- UserService ---

    @Test
    @DisplayName("Kullanıcı bul - başarılı")
    void findUserByIdSuccess() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);

        assertThat(found.getEmail()).isEqualTo("ali@example.com");
    }

    @Test
    @DisplayName("Kullanıcı bul - bulunamadı")
    void findUserByIdNotFound() {
        when(mockUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("User is not found with id: 99");
    }


}
