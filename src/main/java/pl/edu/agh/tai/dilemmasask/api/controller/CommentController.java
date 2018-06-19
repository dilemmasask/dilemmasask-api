package pl.edu.agh.tai.dilemmasask.api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.DTO.CommentDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.CommentsListDTO;
import pl.edu.agh.tai.dilemmasask.api.model.Comment;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.*;
import static pl.edu.agh.tai.dilemmasask.api.controller.PostController.userVotedForPost;

@RestController
public class CommentController {

    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    private final ModelMapper modelMapper;

    private static final User mockUser = new User("mock");

    public CommentController(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.modelMapper = new ModelMapper();

    }
    @GetMapping("/{postId}/comments")
    private ResponseEntity getComments(@PathVariable Long postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userVotedForPost(post, mockUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(getCommentsFromPost(post));
    }

    @PostMapping("/{postId}/comments")
    private ResponseEntity postComment(@PathVariable Long postId, @RequestBody Comment comment){

        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userVotedForPost(post, mockUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        comment.setAuthor(mockUser);
        comment.setCurrentDateTime();
        post.addComment(comment);
        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.OK).body(getCommentsFromPost(post));
    }

    private CommentsListDTO getCommentsFromPost(Post post){
        CommentsListDTO commentsListDTO = new CommentsListDTO();
        post.getComments().forEach(comment -> commentsListDTO.addComment(modelMapper.map(comment, CommentDTO.class)));
        return commentsListDTO;
    }

    @DeleteMapping("/comments/{commentId}")
    private ResponseEntity deleteComment(@PathVariable Long commentId){
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Post post = postRepository.findByCommentsId(commentId);

        if (isUserAllowedToDeleteComment(mockUser, comment)) {
            post.removeComment(comment);
            commentRepository.deleteById(commentId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private boolean isUserAllowedToDeleteComment(User user, Comment comment) {
        return comment.getAuthor().getId().equals(user.getId());
    }

}
