package pl.edu.agh.tai.dilemmasask.api.service.comment;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
import pl.edu.agh.tai.dilemmasask.api.repository.CommentRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.service.CommentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private CommentRepository commentRepository;

    private static ModelMapper modelMapper;

    private Post post1;

    private static User mockUser1;

    @BeforeClass
    public static void beforeAll(){
        modelMapper = new ModelMapper();
        mockUser1 = Mockito.mock(User.class);
    }

    @Before
    public void beforeEach(){
        post1 = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));
    }

    @Test
    public void getCommentsTest_shouldReturnBadRequest_whenNotSuchPost(){
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void getCommentsTest_shouldReturnForbidden_whenUserNotVotedForPost(){
        Post mockPost = Mockito.mock(Post.class);
        Poll mockPoll = Mockito.mock(Poll.class);
        Answer mockAnswer = Mockito.mock(Answer.class);

        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(mockPost.getId()).thenReturn(1L);
        Mockito.when(mockPost.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockPost.getPoll()).thenReturn(mockPoll);
        Mockito.when(mockPoll.getAnswers()).thenReturn(Collections.singletonList(mockAnswer));
        Mockito.when(mockAnswer.getId()).thenReturn(1L);
        Mockito.when(mockUser1.getId()).thenReturn(1L);

        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

    }

    @Test
    public void getCommentsTest_shouldReturnOkAndComments_whenUserVotedForPost(){
        Post mockPost = Mockito.mock(Post.class);
        Poll mockPoll = Mockito.mock(Poll.class);
        Answer mockAnswer = Mockito.mock(Answer.class);
        Comment comment = new Comment(mockUser1, "Good question!");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("Good question!");

        CommentsListDTO commentsListDTO = new CommentsListDTO();
        commentsListDTO.addComment(commentDTO);
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(mockPost.getId()).thenReturn(1L);
        Mockito.when(mockPost.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockPost.getPoll()).thenReturn(mockPoll);
        Mockito.when(mockPoll.getAnswers()).thenReturn(Collections.singletonList(mockAnswer));

        Mockito.when(mockAnswer.getId()).thenReturn(1L);
        Mockito.when(mockUser1.getId()).thenReturn(1L);
        Mockito.when(mockAnswer.getVoters()).thenReturn(new HashSet<>(Collections.singletonList(mockUser1)));
        Mockito.when(mockPost.getComments()).thenReturn(new HashSet<>(Collections.singletonList(comment)));

        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(CommentsListDTO.class, responseEntity.getBody().getClass());
        assertEquals(commentsListDTO.getComments().get(0).getText(), ((CommentsListDTO) responseEntity.getBody()).getComments().get(0).getText());
    }

}
