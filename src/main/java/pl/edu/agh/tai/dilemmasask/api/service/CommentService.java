package pl.edu.agh.tai.dilemmasask.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.edu.agh.tai.dilemmasask.api.DTO.CommentDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.CommentsListDTO;
import pl.edu.agh.tai.dilemmasask.api.model.Comment;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.CommentRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private static final ModelMapper modelMapper = new ModelMapper();

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public ResponseEntity getComments(User user, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userVotedForPost(post, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(getCommentsFromPost(post));
    }

    public ResponseEntity postComment(User user, Long postId, Comment comment) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null || comment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userVotedForPost(post, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        comment.setAuthor(user);
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

    public ResponseEntity deleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Post post = postRepository.findByCommentsId(commentId);

        if (isUserAllowedToDeleteComment(user, comment)) {
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

    private static boolean userVotedForPost(Post post, User user) {
        return post.getPoll().getAnswers().stream().anyMatch(a -> a.getVoters().stream().anyMatch(v->v.getId().equals(user.getId())));
    }
}

