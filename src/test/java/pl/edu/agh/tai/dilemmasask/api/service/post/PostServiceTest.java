package pl.edu.agh.tai.dilemmasask.api.service.post;

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
import pl.edu.agh.tai.dilemmasask.api.DTO.NotVotedPostDTO;
import pl.edu.agh.tai.dilemmasask.api.model.Answer;
import pl.edu.agh.tai.dilemmasask.api.model.Poll;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.service.PostService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    private static ModelMapper modelMapper;

    private Post post1;

    @BeforeClass
    public static void beforeAll(){
        modelMapper = new ModelMapper();
    }

    @Before
    public void beforeEach(){
        post1 = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));
    }

    @Test
    public void getPostTest() {
        User mockUser = Mockito.mock(User.class);
        Optional<Post> optionalPost = Optional.of(post1);
        NotVotedPostDTO notVotedPostDTO = modelMapper.map(post1, NotVotedPostDTO.class);

        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(optionalPost);

        ResponseEntity responseEntity = postService.getPost(mockUser, 1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(notVotedPostDTO.toString(), responseEntity.getBody().toString());
    }

    @Test
    public void addNewPostTest() {
        User mockUser = Mockito.mock(User.class);
        ModelMapper modelMapper = new ModelMapper();
        NotVotedPostDTO notVotedPostDTO = modelMapper.map(post1, NotVotedPostDTO.class);

        ResponseEntity responseEntity = postService.addNewPost(mockUser, post1.getPoll());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(notVotedPostDTO.toString(), responseEntity.getBody().toString());
    }
}
