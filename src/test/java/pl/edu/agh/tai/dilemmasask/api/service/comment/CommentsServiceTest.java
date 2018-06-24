package pl.edu.agh.tai.dilemmasask.api.service.comment;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.agh.tai.dilemmasask.api.DTO.CommentDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.CommentsListDTO;
import pl.edu.agh.tai.dilemmasask.api.model.*;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.service.CommentService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class CommentsServiceTest {
    @Autowired
    private CommentService commentService;

    @MockBean
    private PostRepository postRepository;
    private static final String question = "Good question!";
    private static User mockUser1;
    private static Post mockPost;
    private static Poll mockPoll;
    private static Answer mockAnswer;
    private Comment comment;
    private CommentsListDTO commentsListDTO;

    @BeforeClass
    public static void beforeAll() {
        mockUser1 = Mockito.mock(User.class);
        mockPost = Mockito.mock(Post.class);
        mockPoll = Mockito.mock(Poll.class);
        mockAnswer = Mockito.mock(Answer.class);
    }

    @Before
    public void beforeEach() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText(question);
        commentsListDTO = new CommentsListDTO();
        commentsListDTO.addComment(commentDTO);
        comment = new Comment();
        comment.setText(question);
    }

    @Test
    public void getCommentsTest_shouldReturnBadRequest_whenNotSuchPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void postCommentsTest_shouldReturnBadRequest_whenNotSuchPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity responseEntity = commentService.postComment(mockUser1, 1L, comment);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void getCommentsTest_shouldReturnForbidden_whenUserNotVotedForPost() {
        prepareCommonMocks();
        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

    }
    @Test
    public void postCommentsTest_shouldReturnForbidden_whenUserNotVotedForPost() {
        prepareCommonMocks();
        ResponseEntity responseEntity = commentService.postComment(mockUser1, 1L, comment);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

    }

    @Test
    public void getCommentsTest_shouldReturnOkAndComments_whenUserVotedForPost() {

        prepareMocksWithVoters();

        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(CommentsListDTO.class, responseEntity.getBody().getClass());
        assertEquals(commentsListDTO.getComments().get(0).getText(), ((CommentsListDTO) responseEntity.getBody()).getComments().get(0).getText());
    }

    @Test
    public void postCommentsTest_shouldReturnOkAndComments_whenUserVotedForPost() {

        prepareMocksWithVoters();

        ResponseEntity responseEntity = commentService.postComment(mockUser1, 1L, comment);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(CommentsListDTO.class, responseEntity.getBody().getClass());
        assertEquals(commentsListDTO.getComments().get(0).getText(), ((CommentsListDTO) responseEntity.getBody()).getComments().get(0).getText());
    }

    private void prepareCommonMocks() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(mockPost.getId()).thenReturn(1L);
        Mockito.when(mockPost.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockPost.getPoll()).thenReturn(mockPoll);
        Mockito.when(mockPoll.getAnswers()).thenReturn(Collections.singletonList(mockAnswer));
        Mockito.when(mockUser1.getId()).thenReturn(1L);

    }

    private void prepareMocksWithVoters() {
        prepareCommonMocks();
        Mockito.when(mockAnswer.getVoters()).thenReturn(new HashSet<>(Collections.singletonList(mockUser1)));
        Mockito.when(mockPost.getComments()).thenReturn(new HashSet<>(Collections.singletonList(comment)));
    }

}
