package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Poll {
    private String question;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();


//    @OneToMany
//    private Set<Tag> tags = new HashSet<>();

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

    public void voteAnswer(User voter, long answerId) {
        answers.stream().filter(a -> a.getId().equals(answerId)).findFirst().ifPresent(answer -> answer.vote(voter));
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

//    public Set<Tag> getTags() {
//        return tags;
//    }
//
//    public void setTags(Set<Tag> tags) {
//        this.tags = tags;
//    }
//
//    public void addTag(Tag tag){
//        tags.add(tag);
//    }

    public int getTotalVotes(){
        return answers.stream().mapToInt(Answer::getVotes).sum();
    }


}
