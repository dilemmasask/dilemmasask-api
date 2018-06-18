package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Poll {
    private String question;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL)
    private Set<Tag> tags = new HashSet<>();

    public Poll() {
    }

    public Poll(String question, List<Answer> answers, Set<Tag> tags) {
        this.question = question;
        this.answers = answers;
        this.tags = tags;
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
