package pl.edu.agh.tai.dilemmasask.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User author;

    private String text;

    public Comment() {
    }

    public Comment(User author, String text) {
        this.dateTime = LocalDateTime.now();
        this.author = author;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
