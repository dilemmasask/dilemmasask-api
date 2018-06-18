package pl.edu.agh.tai.dilemmasask.api.DTO;

public class NotVotedPostDTO extends PostDTO{
    private Long id;
    private String dateTime;
    private UserDTO author;
    private NotVotedPollDTO poll;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public NotVotedPollDTO getPoll() {
        return poll;
    }

    public void setPoll(NotVotedPollDTO poll) {
        this.poll = poll;
    }
}
