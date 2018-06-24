package pl.edu.agh.tai.dilemmasask.api.service;

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
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeletePostTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    private static User mockUser1;

    @BeforeClass
    public static void beforeAll(){
        mockUser1 = Mockito.mock(User.class);
    }

    @Test
    public void deletePostTest_shouldReturnBadRequest_whenNotSuchPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity responseEntity = postService.deletePost(1L, mockUser1);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void deletePostTest_shouldReturnForbidden_whenNotUserIsNotAllowedToDeleteThePost() {
        User mockUser2 = Mockito.mock(User.class);

        Post mockPost = Mockito.mock(Post.class);
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(mockPost));
        Mockito.when(mockPost.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockUser1.getId()).thenReturn(1L);
        Mockito.when(mockUser2.getId()).thenReturn(2L);

        ResponseEntity responseEntity = postService.deletePost(1L, mockUser2);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void deletePostTest_shouldReturnOk_whenNotUserIsAllowedToDeleteThePost() {
        Post mockPost = Mockito.mock(Post.class);
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(mockPost));
        Mockito.when(mockPost.getAuthor()).thenReturn(mockUser1);
        Mockito.when(mockUser1.getId()).thenReturn(1L);

        ResponseEntity responseEntity = postService.deletePost(1L, mockUser1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
