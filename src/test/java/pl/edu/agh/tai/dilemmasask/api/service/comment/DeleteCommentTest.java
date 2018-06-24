package pl.edu.agh.tai.dilemmasask.api.service.comment;

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
import pl.edu.agh.tai.dilemmasask.api.model.Comment;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.CommentRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.service.CommentService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class DeleteCommentTest {
    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private PostRepository postRepository;

    private static User mockUser1;
    private static User mockUser2;


    @BeforeClass
    public static void beforeAll() {
        mockUser1 = Mockito.mock(User.class);
        mockUser2 = Mockito.mock(User.class);
    }

    @Test
    public void deleteCommentTest_shouldReturnBadRequest_whenNotSuchComment() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity responseEntity = commentService.getComments(mockUser1, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void getCommentsTest_shouldReturnForbidden_whenUserIsNotAllowedToDeleteComment() {

        Comment mockComment = Mockito.mock(Comment.class);

        Mockito.when(commentRepository.findById(1L))
                .thenReturn(Optional.of(mockComment));

        Mockito.when(mockComment.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockUser1.getId()).thenReturn(1L);
        Mockito.when(mockUser2.getId()).thenReturn(2L);

        ResponseEntity responseEntity = commentService.deleteComment(mockUser2, 1L);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

    }

    @Test
    public void shouldReturnOk_whenUserIsAllowedToDeletePost() {
        Comment mockComment = Mockito.mock(Comment.class);
        Post mockPost = Mockito.mock(Post.class);

        Mockito.when(postRepository.findByCommentsId(1L))
                .thenReturn(mockPost);

        Mockito.when(commentRepository.findById(1L))
                .thenReturn(Optional.of(mockComment));

        Mockito.when(mockPost.getComments()).thenReturn(Collections.singleton(mockComment));

        Mockito.when(mockComment.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockUser1.getId()).thenReturn(1L);

        ResponseEntity responseEntity = commentService.deleteComment(mockUser1, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());}
}

