package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.List;

public class VotedPostDTO extends PostDTO{
    private VotedPollDTO poll;
    private List<CommentDTO> comments;

    public VotedPollDTO getPoll() {
        return poll;
    }

    public void setPoll(VotedPollDTO poll) {
        this.poll = poll;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
