package pl.edu.agh.tai.dilemmasask.api.DTO;

public abstract class PostDTO {
    private Long id;
    private String dateTime;
    private UserDTO author;
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

}
