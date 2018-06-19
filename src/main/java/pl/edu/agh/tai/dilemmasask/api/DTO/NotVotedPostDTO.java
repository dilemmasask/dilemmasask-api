package pl.edu.agh.tai.dilemmasask.api.DTO;

public class NotVotedPostDTO extends PostDTO{
    private NotVotedPollDTO poll;

    public NotVotedPollDTO getPoll() {
        return poll;
    }

    public void setPoll(NotVotedPollDTO poll) {
        this.poll = poll;
    }
}
