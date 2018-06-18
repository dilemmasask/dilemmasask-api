package pl.edu.agh.tai.dilemmasask.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.Comment;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.*;

@RestController
public class CommentController {

    private CommentRepository commentRepository;
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    private final static User mockUser = new User("mock");

    @DeleteMapping("/comments/{commentId}")
    private ResponseEntity deleteComment(@PathVariable Long commentId){
        if (isUserAllowedToDeleteComment(mockUser, commentId)) {
            commentRepository.deleteById(commentId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private boolean isUserAllowedToDeleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment==null){
            return false;
        }
        return comment.getAuthor().equals(user);
    }
}
