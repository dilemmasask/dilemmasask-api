package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.List;

class NotVotedPollDTO extends PollDTO{
    private List<NotVotedAnswerDTO> answers;

    public List<NotVotedAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<NotVotedAnswerDTO> answers) {
        this.answers = answers;
    }

}
