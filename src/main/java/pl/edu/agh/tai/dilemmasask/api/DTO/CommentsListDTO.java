package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.ArrayList;
import java.util.List;

public class CommentsListDTO {
    List<CommentDTO> comments = new ArrayList<>();

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public void addComment(CommentDTO commentDTO){
        comments.add(commentDTO);
    }

    @Override
    public String toString() {
        return "CommentsListDTO{" +
                "comments=" + comments +
                '}';
    }
}
