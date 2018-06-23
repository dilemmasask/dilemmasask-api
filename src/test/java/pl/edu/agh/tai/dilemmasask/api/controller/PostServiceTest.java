package pl.edu.agh.tai.dilemmasask.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @MockBean
    private PostRepository postRepository;
    private static Post post;

    @BeforeClass
    public static void initData(){
        post = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));
    }

    @Test
    public void getPostTest() {
        User mockUser = Mockito.mock(User.class);
        Optional<Post> optionalPost = Optional.of(post);
        ModelMapper modelMapper = new ModelMapper();
        NotVotedPostDTO notVotedPostDTO = modelMapper.map(post, NotVotedPostDTO.class);

        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(optionalPost);

        ResponseEntity responseEntity = postService.getPost(mockUser, 1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(notVotedPostDTO.toString(), responseEntity.getBody().toString());
        }

}
