package pl.edu.agh.tai.dilemmasask.api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.DTO.*;
import pl.edu.agh.tai.dilemmasask.api.model.*;
import pl.edu.agh.tai.dilemmasask.api.repository.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@RestController
@RequestMapping("/posts")
public class PostController {
    //TODO user
    private PostRepository postRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private TagsRepository tagsRepository;
    private final ModelMapper modelMapper;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static User mockUser = new User("mock");

    public PostController(PostRepository postRepository, AnswerRepository answerRepository, UserRepository userRepository, CommentRepository commentRepository, TagsRepository tagsRepository) {
        this.postRepository = postRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.tagsRepository = tagsRepository;
        this.modelMapper = new ModelMapper();
        userRepository.save(mockUser);
    }

    @GetMapping
    private ResponseEntity getPolls(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "per_page", defaultValue = "10") Integer pollsPerPage,
            @RequestParam(value = "sort", defaultValue = "new") String sortBy,
            @RequestParam(value = "from", required = false) String dateFrom,
            @RequestParam(value = "to", required = false) String dateTo,
            @RequestParam(value = "tag", required = false) String tag) {

        if(pageNumber < 1 || pollsPerPage < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User user = mockUser;
        Page<Post> posts;
        switch (sortBy){
            case "new":
                posts = getSortedPosts(pageNumber, dateFrom, dateTo, pollsPerPage, tag, "dateTime");
                break;
            case "top":
                posts = getSortedPosts(pageNumber, dateFrom, dateTo, pollsPerPage, tag, "totalVotes");
                break;
            default:
                posts = getRandomPosts(pageNumber, pollsPerPage, tag);
        }
        PostsListDTO postsListDTO = new PostsListDTO();

        posts.getContent().forEach(post -> postsListDTO.addPost(getProperPost(post, user)));

        return ResponseEntity.status(HttpStatus.OK).body(postsListDTO);
    }

    private PostDTO getProperPost(Post post, User user) {
        if(userVotedForPost(post, user)){
            VotedPostDTO votedPostDTO = modelMapper.map(post, VotedPostDTO.class);
            votedPostDTO.setVotedAnswerId(getVotedAnswerId(post, user));
            return votedPostDTO;
        } else {
            return modelMapper.map(post, NotVotedPostDTO.class);
        }
    }

    private Page<Post> getSortedPosts(Integer pageNumber, String from, String to, Integer pollsPerPage, String tag, String sortBy) {

        LocalDateTime dateFrom = LocalDateTime.parse(from, formatter);
        LocalDateTime dateTo = LocalDateTime.parse(to, formatter);

        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.DESC, sortBy));
        if(tag != null){
            return postRepository.findByTagsNameAndDateTimeBetween(tag, dateFrom, dateTo, pageable);
        } else {
            return postRepository.findByDateTimeBetween(dateFrom, dateTo, pageable);
        }
    }

    private Page<Post> getRandomPosts(Integer pageNumber, Integer pollsPerPage, String tag) {
        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage);
        if(tag != null) {
            return postRepository.findByTagsName(tag, pageable);
        } else {
            return postRepository.findAll(pageable);
        }
    }

    @GetMapping("/{postId}")
    private ResponseEntity getPost(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        User user = mockUser;
        if(post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(getProperPost(post, user));
    }

    @PostMapping
    private ResponseEntity addNewPost(@RequestBody Poll poll){
        User user = mockUser;
        if(poll.getQuestion() == null || poll.getQuestion().isEmpty() || poll.getAnswers() == null || poll.getAnswers().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Post post = new Post(user, poll);
        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(post, NotVotedPostDTO.class));
    }

    @DeleteMapping("/{postId}")
    private ResponseEntity deletePost(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        User user = mockUser;
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userIsAllowedToDeletePost(post, user)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postRepository.deleteById(postId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private boolean userIsAllowedToDeletePost(Post post, User user) {
        return post.getAuthor().getId().equals(user.getId());
    }

    @PutMapping("/{postId}/vote/{answerId}")
    private ResponseEntity vote(@PathVariable Long postId, @PathVariable final Long answerId){
        Post post = postRepository.findById(postId).orElse(null);
        User user = mockUser;
        if (post == null || !postContainsAnswer(post, answerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        userRepository.save(user);
        if(!userVotedForPost(post, user)){
            post.voteAnswer(user, answerId);
            postRepository.save(post);
        }

        VotedPostDTO votedPostDTO = modelMapper.map(post, VotedPostDTO.class);
        votedPostDTO.setVotedAnswerId(getVotedAnswerId(post, user));
        return ResponseEntity.status(HttpStatus.OK).body(votedPostDTO);
    }

    private boolean postContainsAnswer(Post post, Long answerId) {
        return post.getPoll().getAnswers().stream().anyMatch(a -> a.getId().equals(answerId));
    }

    public static boolean userVotedForPost(Post post, User user) {
        return post.getPoll().getAnswers().stream().anyMatch(a -> a.getVoters().stream().anyMatch(v->v.getId().equals(user.getId())));
    }

    private Long getVotedAnswerId(Post post, User user) {
        return post.getPoll().getAnswers()
                .stream()
                .filter(answer -> answer.getVoters()
                        .stream()
                        .anyMatch(v-> v.getId().equals(user.getId())))
                .findFirst().get().getId();
    }

}
