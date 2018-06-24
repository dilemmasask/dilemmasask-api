package pl.edu.agh.tai.dilemmasask.api.DTO;

public class VotedAnswerDTO extends NotVotedAnswerDTO{
    private Integer votes;

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "VotedAnswerDTO{" +
                "votes=" + votes +
                '}';
    }
}
