package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private User author;

    @Embedded
    private Poll poll;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    private int totalVotes;

    @OneToMany
    private Set<Tag> tags = new HashSet<>();

    public Post() {}

    public Post(User author, Poll poll) {
        this.dateTime = LocalDateTime.now();
        this.author = author;
        this.poll = poll;
    }
    public Post(LocalDateTime dateTime, User author, Poll poll) {
        this.dateTime = dateTime;
        this.author = author;
        this.poll = poll;
    }

    public Post(LocalDateTime dateTime, User author, Poll poll, Set<Tag> tags) {
        this.dateTime = dateTime;
        this.author = author;
        this.poll = poll;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void voteAnswer(User voter, long answerId){
        poll.voteAnswer(voter, answerId);
        totalVotes = poll.getTotalVotes();
    }

    public void addTag(Tag t) {
        tags.add(t);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", author=" + author +
                ", poll=" + poll +
                ", comments=" + comments +
                ", totalVotes=" + totalVotes +
                ", tags=" + tags +
                '}';
    }
}
