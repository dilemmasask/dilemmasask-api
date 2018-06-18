package pl.edu.agh.tai.dilemmasask.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.repository.*;

@RestController
public class CommentController {

    private CommentRepository commentRepository;
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @DeleteMapping("/comments/{commentId}")
    private ResponseEntity deleteComment(@PathVariable Long commentId){
        commentRepository.deleteById(commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
