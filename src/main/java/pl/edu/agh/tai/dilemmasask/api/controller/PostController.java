package pl.edu.agh.tai.dilemmasask.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.repository.AnswerRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.UserRepository;

@RestController
@RequestMapping("/post")
public class PostController {

    private PostRepository postRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;

    public PostController(PostRepository postRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    private ResponseEntity getNewestPolls(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "per_page", defaultValue = "10") Integer pollsPerPage) {

        if(pageNumber < 1 || pollsPerPage < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Page<Post> posts = postRepository.findAll(
                PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.DESC, "dateTime")));
        return ResponseEntity.status(HttpStatus.OK).body(posts.getContent());
    }

    @PostMapping
    private Post addNewPost(@RequestBody Post post){
        return postRepository.save(post);
    }

    @PostMapping("/{postId}/answer/{answerId}/vote")
    private Post vote(@PathVariable Long postId, @PathVariable Long answerId){
        Post post = postRepository.findById(postId).get();
        post.getPoll().voteAnswer(answerId);
        return post;
    }
}
