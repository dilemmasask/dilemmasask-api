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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.agh.tai.dilemmasask.api.DTO.NotVotedPostDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.PostsListDTO;
import pl.edu.agh.tai.dilemmasask.api.model.*;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.service.PostService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    private static ModelMapper modelMapper;

    private static User mockUser;

    private static String tag = "funny";

    private static LocalDateTime dateFrom;

    private static LocalDateTime dateTo;

    private Post post1;

    private Post post2;

    private List<Post> posts;

    @BeforeClass
    public static void beforeAll(){
        mockUser = Mockito.mock(User.class);
        modelMapper = new ModelMapper();
        dateFrom = LocalDateTime.of(2015, 1, 1, 0, 0, 0);
        dateTo = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
    }

    @Before
    public void beforeEach(){
        post1 = new Post(
                LocalDateTime.of(2016, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("1",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));

        post2 = new Post(
                LocalDateTime.of(2017, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("2",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));

        posts = Arrays.asList(post1, post2);

       }

    @Test
    public void getPostTest() {
        NotVotedPostDTO notVotedPostDTO = modelMapper.map(post1, NotVotedPostDTO.class);

        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(post1));

        ResponseEntity responseEntity = postService.getPost(mockUser, 1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(notVotedPostDTO.toString(), responseEntity.getBody().toString());
    }

    @Test
    public void getNewPostsTest() {
        Pageable pageable = PageRequest.of(0, 2, new Sort(Sort.Direction.DESC, "dateTime"));
        List<Post> posts = Arrays.asList(post1, post2);

        Page<Post> page = new PageImpl<>(posts, pageable, 2);

        Mockito.when(postRepository.findByDateTimeBetween(dateFrom, dateTo, pageable))
                .thenReturn(page);

        ResponseEntity responseEntity = postService.getPosts(mockUser, 1,2, "new", dateFrom, dateTo, null);
        assertPosts(responseEntity);
    }

    @Test
    public void getNewPostsWithTagTest() {
        Pageable pageable = PageRequest.of(0, 2, new Sort(Sort.Direction.DESC, "dateTime"));
        posts.forEach(p -> p.addTag(new Tag(tag)));

        Page<Post> page = new PageImpl<>(posts, pageable, 2);

        Mockito.when(postRepository.findByTagsNameAndDateTimeBetween(tag, dateFrom, dateTo, pageable))
                .thenReturn(page);

        ResponseEntity responseEntity = postService.getPosts(mockUser, 1,2, "new", dateFrom, dateTo, tag);
        assertPosts(responseEntity);
    }

    @Test
    public void getTopPostsTest() {
        Pageable pageable = PageRequest.of(0, 2, new Sort(Sort.Direction.DESC, "totalVotes"));
        Page<Post> page = new PageImpl<>(posts, pageable, 2);

        Mockito.when(postRepository.findByDateTimeBetween(dateFrom, dateTo, pageable))
                .thenReturn(page);

        ResponseEntity responseEntity = postService.getPosts(mockUser, 1,2, "top", dateFrom, dateTo, null);
        assertPosts(responseEntity);
    }

    @Test
    public void getTopPostsWithTagTest() {
        Pageable pageable = PageRequest.of(0, 2, new Sort(Sort.Direction.DESC, "totalVotes"));
        posts.forEach(p -> p.addTag(new Tag(tag)));
        Page<Post> page = new PageImpl<>(posts, pageable, 2);

        Mockito.when(postRepository.findByTagsNameAndDateTimeBetween(tag, dateFrom, dateTo, pageable))
                .thenReturn(page);

        ResponseEntity responseEntity = postService.getPosts(mockUser, 1,2, "top", dateFrom, dateTo, tag);
        assertPosts(responseEntity);

    }

    private void assertPosts(ResponseEntity responseEntity){
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, ((PostsListDTO) responseEntity.getBody()).getPosts().size());
        assertTrue(((PostsListDTO) responseEntity.getBody()).getPosts().stream().anyMatch(postDTO -> ((NotVotedPostDTO)postDTO).getPoll().getQuestion().equals(post1.getPoll().getQuestion())));
        assertTrue(((PostsListDTO) responseEntity.getBody()).getPosts().stream().anyMatch(postDTO -> ((NotVotedPostDTO)postDTO).getPoll().getQuestion().equals(post2.getPoll().getQuestion())));

    }

    @Test
    public void addNewPostTest() {
        NotVotedPostDTO notVotedPostDTO = modelMapper.map(post1, NotVotedPostDTO.class);

        ResponseEntity responseEntity = postService.addNewPost(mockUser, post1.getPoll());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(notVotedPostDTO.toString(), responseEntity.getBody().toString());
    }
}
