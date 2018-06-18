package pl.edu.agh.tai.dilemmasask.api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.DTO.VotedPostDTO;
import pl.edu.agh.tai.dilemmasask.api.model.*;
import pl.edu.agh.tai.dilemmasask.api.repository.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    //TODO user
    private PostRepository postRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private TagsRepository tagsRepository;
    private ModelMapper modelMapper;

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
            @RequestParam(value = "sort", defaultValue = "") String sortBy,
            @RequestParam(value = "from", required = false) String dateFrom,
            @RequestParam(value = "to", required = false) String dateTo,
            @RequestParam(value = "tag", required = false) String tag) {

        if(pageNumber < 1 || pollsPerPage < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Page<Post> posts;
        switch (sortBy){
            case "new":
                posts = getNewPosts(pageNumber, dateFrom, dateTo, pollsPerPage, tag);
                break;
            case "top":
                posts = getTopPosts(pageNumber, pollsPerPage, tag);
                break;
            default:
                posts = getRandomPosts(pageNumber, pollsPerPage);

        }
        return ResponseEntity.status(HttpStatus.OK).body(posts.getContent());
    }

    private Page<Post> getNewPosts(Integer pageNumber, String dateFrom, String dateTo, Integer pollsPerPage, String tag) {
        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.ASC, "dateTime"));
        Page<Post> posts = postRepository.findByTagsName(tag, pageable);
        return posts;
    }

    private Page<Post> getTopPosts(Integer pageNumber, Integer pollsPerPage, String tag) {
        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.ASC, "totalVotes"));
        Page<Post> posts = postRepository.findByTagsName(tag, pageable);
        return posts;
    }

    private Page<Post> getRandomPosts(Integer pageNumber, Integer pollsPerPage) {
        return null;
    }

    @GetMapping("/{postId}")
    private ResponseEntity getPost(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post!=null){
            VotedPostDTO votedPostDTO = modelMapper.map(post, VotedPostDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(votedPostDTO);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping
    private Post addNewPost(@RequestBody Post post){
        return postRepository.save(post);
    }

    @DeleteMapping("/{postId}")
    private ResponseEntity deletePost(@PathVariable Long postId){
        postRepository.deleteById(postId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{postId}/vote/{answerId}")
    private ResponseEntity vote(@PathVariable Long postId, @PathVariable Long answerId){
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            post.voteAnswer(answerId);
            return ResponseEntity.status(HttpStatus.OK).body(post);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/{postId}/comments")
    private ResponseEntity getComments(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post!=null){
            return ResponseEntity.status(HttpStatus.OK).body(post.getComments());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/{postId}/comments")
    private ResponseEntity postComment(@PathVariable Long postId, @RequestBody String text){
        Post post = postRepository.findById(postId).orElse(null);
        if(post!=null){
            Comment comment = new Comment(new User(), text);
            commentRepository.save(comment);
            post.addComment(comment);
            return ResponseEntity.status(HttpStatus.OK).body(post.getComments());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    private ResponseEntity deleteComment(@PathVariable Long postId, @PathVariable Long commentId){
        //is postId necessary?
        commentRepository.deleteById(commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
