package pl.edu.agh.tai.dilemmasask.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.Comment;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.UserRepository;
import pl.edu.agh.tai.dilemmasask.api.service.CommentService;

import javax.validation.constraints.NotNull;

@RestController
public class CommentController {

    private UserRepository userRepository;
    private CommentService commentService;

    public CommentController(UserRepository userRepository, CommentService commentService) {
        this.userRepository = userRepository;
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comment")
    private ResponseEntity getComments(@AuthenticationPrincipal User principal, @PathVariable Long postId){
        User user = getLoggedUser(principal);
        return commentService.getComments(user, postId);
    }

    @PostMapping("/posts/{postId}/comment")
    private ResponseEntity postComment(@AuthenticationPrincipal User principal, @PathVariable Long postId, @RequestBody @NotNull Comment comment){
        User user = getLoggedUser(principal);
        return commentService.postComment(user, postId, comment);
    }

    @DeleteMapping("/comment/{commentId}")
    private ResponseEntity deleteComment(@AuthenticationPrincipal User principal, @PathVariable Long commentId){
        User user = getLoggedUser(principal);
        return commentService.deleteComment(user, commentId);
    }

    private User getLoggedUser(User principal) {
        return principal == null ? null : userRepository.findByPrincipalId(principal.getPrincipalId());
    }

}
