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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        posts.getContent().forEach(post -> postsListDTO.addPost(getProperPost(post, mockUser)));

        return ResponseEntity.status(HttpStatus.OK).body(postsListDTO);
    }

    private PostDTO getProperPost(Post post, User user) {
        if(userVotedForPost(post, user)){
            return modelMapper.map(post, VotedPostDTO.class);
        } else {
            return modelMapper.map(post, NotVotedPostDTO.class);
        }
    }

    private Page<Post> getSortedPosts(Integer pageNumber, String from, String to, Integer pollsPerPage, String tag, String sortBy) {

        LocalDateTime dateFrom = LocalDateTime.parse(from, formatter);
        LocalDateTime dateTo = LocalDateTime.parse(to, formatter);

        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.DESC, sortBy));
        return postRepository.findByTagsNameAndDateTimeBetween(tag, dateFrom, dateTo, pageable);
    }

    private Page<Post> getRandomPosts(Integer pageNumber, Integer pollsPerPage, String tag) {
        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage);
        return postRepository.findByTagsName(tag, pageable);
    }

    @GetMapping("/{postId}")
    private ResponseEntity getPost(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PostDTO postDTO;
        if(userVotedForPost(post, mockUser)){
            postDTO = modelMapper.map(post, VotedPostDTO.class);
        } else {
            postDTO = modelMapper.map(post, NotVotedPostDTO.class);
        }
        return ResponseEntity.status(HttpStatus.OK).body(postDTO);

    }

    @PostMapping
    private ResponseEntity addNewPost(@RequestBody Post post){
        //TODO
        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(post, NotVotedPostDTO.class));
    }

    @DeleteMapping("/{postId}")
    private ResponseEntity deletePost(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userIsAllowedToDeletePost(post, mockUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postRepository.deleteById(postId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private boolean userIsAllowedToDeletePost(Post post, User user) {
        return post.getAuthor().equals(user);
    }

    @PutMapping("/{postId}/vote/{answerId}")
    private ResponseEntity vote(@PathVariable Long postId, @PathVariable Long answerId){
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;

        if(userVotedForPost(post, mockUser)){
            answerId = getVotedAnswerId(post, mockUser);
        } else{
            post.voteAnswer(mockUser, answerId);
            httpStatus = HttpStatus.OK;
        }
        VotedPostDTO votedPostDTO = modelMapper.map(post, VotedPostDTO.class);
        votedPostDTO.setVotedAnswerId(answerId);
        return ResponseEntity.status(httpStatus).body(votedPostDTO);

    }

    private boolean userVotedForPost(Post post, User user) {
        return post.getPoll().getAnswers().stream().anyMatch(a -> a.getVoters().contains(user));
    }

    private Long getVotedAnswerId(Post post, User user) {
        return post.getPoll().getAnswers().stream().filter(answer -> answer.getVoters().contains(user)).findFirst().get().getId();
    }

    @GetMapping("/{postId}/comments")
    private ResponseEntity getComments(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!isPostVoted(post, mockUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(getCommentsFromPost(post));
    }

    @PostMapping("/{postId}/comments")
    private ResponseEntity postComment(@PathVariable Long postId, @RequestBody String text){
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!isPostVoted(post, mockUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Comment comment = new Comment(mockUser, text);
        commentRepository.save(comment);
        post.addComment(comment);
        return ResponseEntity.status(HttpStatus.OK).body(getCommentsFromPost(post));
    }

    private CommentsListDTO getCommentsFromPost(Post post){
        CommentsListDTO commentsListDTO = new CommentsListDTO();
        post.getComments().forEach(comment -> commentsListDTO.addComment(modelMapper.map(comment, CommentDTO.class)));
        return commentsListDTO;
    }

    private boolean isPostVoted(Post post, User user) {
        //TODO
        return false;
    }
}
