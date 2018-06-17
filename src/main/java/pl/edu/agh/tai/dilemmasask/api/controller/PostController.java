package pl.edu.agh.tai.dilemmasask.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.Comment;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.AnswerRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.CommentRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.UserRepository;

@RestController
@RequestMapping("/posts")
public class PostController {
    //TODO user
    private PostRepository postRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;

    public PostController(PostRepository postRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    private ResponseEntity getNewestPolls(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "per_page", defaultValue = "10") Integer pollsPerPage,
            @RequestParam(value = "sort", defaultValue = "") String sortBy,
            @RequestParam(value = "from", required = false) String dateFrom,
            @RequestParam(value = "to", required = false) String dateTo,
            @RequestParam(value = "tag", required = false) String tag) {

        if(pageNumber < 1 || pollsPerPage < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Page<Post> posts = postRepository.findAll(
                PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.DESC, "dateTime")));
        return ResponseEntity.status(HttpStatus.OK).body(posts.getContent());
    }

    @GetMapping("/{postId}")
    private ResponseEntity getPost(@PathVariable Long postId){
        //TODO: postDTO
        return null;
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
            post.getPoll().voteAnswer(answerId);
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
