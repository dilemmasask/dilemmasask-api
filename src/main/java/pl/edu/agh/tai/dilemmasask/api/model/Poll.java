package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Embeddable
public class Poll {
    @NotNull
    @NotEmpty
    private String question;

    @NotNull
    @NotEmpty
    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();

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

    void voteAnswer(User voter, long answerId) {
        answers.stream().filter(a -> a.getId().equals(answerId)).findFirst().ifPresent(answer -> answer.vote(voter));
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag){
        tags.add(tag);
    }

    int getTotalVotes(){
        return answers.stream().mapToInt(Answer::getVotes).sum();
    }

}
