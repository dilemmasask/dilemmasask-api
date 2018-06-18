package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.List;

public class VotedPollDTO extends PollDTO{
    private List<VotedAnswerDTO> answers;
    private Long votedAnswerId;

    public List<VotedAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<VotedAnswerDTO> answers) {
        this.answers = answers;
    }

    public Long getVotedAnswerId() {
        return votedAnswerId;
    }

    public void setVotedAnswerId(Long votedAnswerId) {
        this.votedAnswerId = votedAnswerId;
    }

}
