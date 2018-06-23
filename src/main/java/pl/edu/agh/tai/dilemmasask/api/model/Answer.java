package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    private String text;
    private int votes = 0;

    @ManyToMany
    private Set<User> voters = new HashSet<>();

    public Answer() {
    }

    public Answer(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    void vote(User voter) {
        voters.add(voter);
        voter.addAnswer(this);
        votes++;
    }

    public Set<User> getVoters() {
        return voters;
    }

    public void setVoters(Set<User> voters) {
        this.voters = voters;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", votes=" + votes +
                ", voters=" + voters +
                '}';
    }
}
