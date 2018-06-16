package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Poll {
    private String question;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    public Poll() {
    }

    public Poll(String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void voteAnswer(long answerId) {
        answers.stream().filter(a -> a.getId().equals(answerId)).findFirst().ifPresent(Answer::incrementVotes);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
