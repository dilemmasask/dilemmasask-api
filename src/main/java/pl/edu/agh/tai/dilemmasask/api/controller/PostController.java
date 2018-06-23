package pl.edu.agh.tai.dilemmasask.api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.Poll;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.UserRepository;
import pl.edu.agh.tai.dilemmasask.api.service.PostService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/posts")
public class PostController {

    private UserRepository userRepository;
    private PostService postService;
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PostController(UserRepository userRepository, PostService postService) {
        this.userRepository = userRepository;
        this.postService = postService;
    }

    @GetMapping
    private ResponseEntity getPosts(@AuthenticationPrincipal User principal,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "per_page", defaultValue = "10") Integer pollsPerPage,
            @RequestParam(value = "sort", defaultValue = "random") String sortBy,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "tag", required = false) String tag) {
        User user = getLoggedUser(principal);

        if(pageNumber < 1 || pollsPerPage < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LocalDateTime dateFrom = LocalDateTime.parse(from, formatter);
        LocalDateTime dateTo = LocalDateTime.parse(to, formatter);

        return postService.getPosts(user, pageNumber, pollsPerPage, sortBy, dateFrom, dateTo, tag);
    }

    @GetMapping("/{postId}")
    private ResponseEntity getPost(@AuthenticationPrincipal User principal, @PathVariable Long postId){
        User user = getLoggedUser(principal);
        return postService.getPost(user, postId);
    }

    @PostMapping
    private ResponseEntity addNewPost(@AuthenticationPrincipal User principal, @RequestBody Poll poll){
        User user = getLoggedUser(principal);
        return postService.addNewPost(user, poll);
    }

    @DeleteMapping("/{postId}")
    private ResponseEntity deletePost(@AuthenticationPrincipal User principal, @PathVariable Long postId){
        User user = getLoggedUser(principal);
        return postService.deletePost(postId, user);
    }

    @PutMapping("/{postId}/vote/{answerId}")
    private ResponseEntity vote(@AuthenticationPrincipal User principal, @PathVariable Long postId, @PathVariable final Long answerId){
        User user = getLoggedUser(principal);
        return postService.vote(user, postId, answerId);
    }

    private User getLoggedUser(User principal) {
        return principal == null ? null : userRepository.findByPrincipalId(principal.getPrincipalId());
    }

}
