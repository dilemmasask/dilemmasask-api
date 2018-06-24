package pl.edu.agh.tai.dilemmasask.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import pl.edu.agh.tai.dilemmasask.api.service.PostService;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private Post post;

    @Before
    public void beforeEach(){
        post = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));
    }

    @Test
    public void getSinglePostTest() throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        NotVotedPostDTO notVotedPostDTO = modelMapper.map(post, NotVotedPostDTO.class);

        Mockito.when(postService.getPost(eq(null), Mockito.anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(notVotedPostDTO));

        mockMvc.perform(get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(notVotedPostDTO))));
    }
}