package pl.edu.agh.tai.dilemmasask.api.service;

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
import pl.edu.agh.tai.dilemmasask.api.DTO.VotedPostDTO;
import pl.edu.agh.tai.dilemmasask.api.model.Answer;
import pl.edu.agh.tai.dilemmasask.api.model.Poll;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class VoteTest {

    @Autowired
    private PostService postService;
    @MockBean
    private PostRepository postRepository;

    private static User mockUser;
    private static ModelMapper modelMapper;
    private Post post1;
    private Post mockPost;
    private Poll mockPoll;
    private Answer mockAnswer;

    @BeforeClass
    public static void beforeAll(){
        modelMapper = new ModelMapper();
        mockUser = Mockito.mock(User.class);
    }
    @Before
    public void beforeEach(){
        post1 = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44), new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));

        mockPost = Mockito.mock(Post.class);
        mockPoll = Mockito.mock(Poll.class);
        mockAnswer = Mockito.mock(Answer.class);
    }

    @Test
    public void voteTest_shouldReturnBadRequest_whenNotSuchPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(mockPost.getId()).thenReturn(1L);
        Mockito.when(mockPost.getPoll()).thenReturn(mockPoll);
        Mockito.when(mockPoll.getAnswers()).thenReturn(Collections.singletonList(mockAnswer));
        Mockito.when(mockAnswer.getId()).thenReturn(1L);

        ResponseEntity responseEntity = postService.vote(mockUser, 2L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void voteTest_shouldReturnBadRequest_whenNotSuchAnswerInPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(mockPost.getId()).thenReturn(1L);
        Mockito.when(mockPost.getPoll()).thenReturn(mockPoll);
        Mockito.when(mockPoll.getAnswers()).thenReturn(Collections.singletonList(mockAnswer));
        Mockito.when(mockAnswer.getId()).thenReturn(1L);

        ResponseEntity responseEntity = postService.vote(mockUser, 1L, 2L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void voteTest_shouldReturnOKAndVotedPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(mockPost.getId()).thenReturn(1L);
        Mockito.when(mockPost.getPoll()).thenReturn(mockPoll);
        Mockito.when(mockPoll.getAnswers()).thenReturn(Collections.singletonList(mockAnswer));
        Mockito.when(mockAnswer.getId()).thenReturn(1L);
        Mockito.when(mockAnswer.getVotes()).thenReturn(1);

        Mockito.when(mockAnswer.getVoters()).thenReturn(new HashSet<>(Collections.singletonList(mockUser)));

        Mockito.when(mockUser.getId()).thenReturn(1L);

        VotedPostDTO votedPostDTO = modelMapper.map(post1, VotedPostDTO.class);
        votedPostDTO.setVotedAnswerId(1L);
        votedPostDTO.getPoll().getAnswers().get(0).setVotes(1);

        ResponseEntity responseEntity = postService.vote(mockUser, 1L, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(votedPostDTO.getVotedAnswerId(), ((VotedPostDTO) responseEntity.getBody()).getVotedAnswerId());
        assertEquals(Integer.valueOf(1), ((VotedPostDTO) responseEntity.getBody()).getPoll().getAnswers().get(0).getVotes());

    }
}
