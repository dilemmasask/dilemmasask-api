package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.List;

public abstract class PollDTO {
    private String question;

    private List<TagDTO> tags;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }
}
